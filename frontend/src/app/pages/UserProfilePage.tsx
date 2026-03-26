import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, useSearchParams } from 'react-router';
import { Opportunity, Company, Tag, Application, statusMap } from '../types';
import { Button } from '../components/ui/button';
import { Badge } from '../components/ui/badge';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { Avatar, AvatarFallback } from '../components/ui/avatar';
import { appApi, UserProfile, FriendStatus } from '../api/appApi';
import { useAuth } from '../context/AuthContext';
import { toast } from 'sonner';
import { ArrowLeft, User as UserIcon, Briefcase, Star, Building2, MapPin, Calendar, CheckCircle, Clock, XCircle } from 'lucide-react';

export const UserProfilePage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { currentUser } = useAuth();
  
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [companies, setCompanies] = useState<Company[]>([]);
  const [opportunities, setOpportunities] = useState<Opportunity[]>([]);
  const [friendStatus, setFriendStatus] = useState<FriendStatus | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isProcessing, setIsProcessing] = useState(false);

  useEffect(() => {
    async function load() {
      if (!id) return;
      
      try {
        const [profileData, companiesData, opportunitiesData, statusData] = await Promise.all([
          appApi.getUserProfile(id),
          appApi.getCompanies(),
          appApi.getOpportunities(),
          currentUser?.role === 'applicant' ? appApi.getFriendStatus(id) : Promise.resolve({ status: 'none' } as FriendStatus),
        ]);
        
        setProfile(profileData);
        setCompanies(companiesData);
        setOpportunities(opportunitiesData);
        setFriendStatus(statusData);
      } catch (error) {
        toast.error('Не удалось загрузить профиль пользователя');
        navigate('/');
      } finally {
        setIsLoading(false);
      }
    }

    if (currentUser) {
      void load();
    }
  }, [id, currentUser, navigate]);

  const handleSendRequest = async () => {
    setIsProcessing(true);
    try {
      await appApi.sendFriendRequest(id!);
      setFriendStatus({ status: 'sent' });
      toast.success('Запрос в друзья отправлен');
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Не удалось отправить запрос');
    } finally {
      setIsProcessing(false);
    }
  };

  const handleAcceptRequest = async () => {
    setIsProcessing(true);
    try {
      await appApi.acceptFriendRequest(id!);
      setFriendStatus({ status: 'friends' });
      toast.success('Запрос принят');
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Не удалось принять запрос');
    } finally {
      setIsProcessing(false);
    }
  };

  const handleRejectRequest = async () => {
    setIsProcessing(true);
    try {
      await appApi.rejectFriendRequest(id!);
      setFriendStatus({ status: 'none' });
      toast.success('Запрос отклонен');
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Не удалось отклонить запрос');
    } finally {
      setIsProcessing(false);
    }
  };

  const handleCancelRequest = async () => {
    setIsProcessing(true);
    try {
      await appApi.cancelFriendRequest(id!);
      setFriendStatus({ status: 'none' });
      toast.success('Запрос отменен');
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Не удалось отменить запрос');
    } finally {
      setIsProcessing(false);
    }
  };

  const handleRemoveFriend = async () => {
    setIsProcessing(true);
    try {
      await appApi.removeFriend(id!);
      setFriendStatus({ status: 'none' });
      toast.success('Пользователь удален из друзей');
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Не удалось удалить из друзей');
    } finally {
      setIsProcessing(false);
    }
  };

  const handleGoBack = () => {
    const from = searchParams.get('from');
    if (from === 'user' && searchParams.get('userId')) {
      navigate(`/user/${searchParams.get('userId')}`);
    } else if (from === 'user') {
      navigate('/dashboard/applicant?tab=friends');
    } else {
      navigate(-1);
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-gray-500">Загрузка...</div>
      </div>
    );
  }

  if (!profile) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-gray-500">Пользователь не найден</div>
      </div>
    );
  }

  const { user, isFriend, showResume, showApplications, favorites, applications } = profile;

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white border-b sticky top-0 z-10">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
              <Button variant="ghost" size="icon" onClick={handleGoBack}>
                <ArrowLeft className="w-5 h-5" />
              </Button>
              <div 
                className="w-10 h-10 bg-gradient-to-br from-blue-500 to-purple-600 rounded-lg flex items-center justify-center cursor-pointer"
                onClick={() => navigate('/')}
              >
                <span className="text-white font-bold text-xl">Т</span>
              </div>
              <h1 className="text-2xl font-bold">Трамплин</h1>
            </div>

            <div className="flex items-center gap-3">
              {currentUser ? (
                <Button
                  onClick={() => {
                    const path = currentUser.role === 'applicant' 
                      ? '/dashboard/applicant' 
                      : currentUser.role === 'employer' 
                        ? '/dashboard/employer' 
                        : '/dashboard/curator';
                    navigate(path);
                  }}
                >
                  Личный кабинет
                </Button>
              ) : (
                <>
                  <Button variant="outline" onClick={() => navigate('/login')}>
                    Войти
                  </Button>
                  <Button onClick={() => navigate('/register')}>
                    Регистрация
                  </Button>
                </>
              )}
            </div>
          </div>
        </div>
      </header>

      <main className="container mx-auto px-4 py-8">
        <div className="max-w-4xl mx-auto">
          <Card className="mb-6">
            <CardContent className="pt-6">
              <div className="flex items-start gap-6">
                <Avatar className="w-24 h-24">
                  <AvatarFallback className="text-3xl bg-gradient-to-br from-blue-500 to-purple-600 text-white">
                    {user.displayName.charAt(0)}
                  </AvatarFallback>
                </Avatar>
                
                <div className="flex-1">
                  <div className="flex justify-between items-start">
                    <div>
                      <h2 className="text-2xl font-bold">{user.displayName}</h2>
                      {user.fullName && <p className="text-gray-600">{user.fullName}</p>}
                      {user.university && <p className="text-gray-500 text-sm">{user.university}</p>}
                    </div>
                    
                    {currentUser?.role === 'applicant' && currentUser.id !== id && (
                      <div className="flex gap-2">
                        {friendStatus?.status === 'none' && (
                          <Button onClick={handleSendRequest} disabled={isProcessing}>
                            <UserIcon className="w-4 h-4 mr-2" />
                            Добавить в друзья
                          </Button>
                        )}
                        {friendStatus?.status === 'pending' && (
                          <Button onClick={handleAcceptRequest} disabled={isProcessing}>
                            <CheckCircle className="w-4 h-4 mr-2" />
                            Принять
                          </Button>
                        )}
                        {friendStatus?.status === 'pending' && (
                          <Button variant="outline" onClick={handleRejectRequest} disabled={isProcessing}>
                            Отклонить
                          </Button>
                        )}
                        {friendStatus?.status === 'sent' && (
                          <Button variant="outline" onClick={handleCancelRequest} disabled={isProcessing}>
                            Отменить запрос
                          </Button>
                        )}
                        {friendStatus?.status === 'friends' && (
                          <Button variant="outline" onClick={handleRemoveFriend} disabled={isProcessing}>
                            Удалить из друзей
                          </Button>
                        )}
                      </div>
                    )}
                  </div>

                  {user.skills && user.skills.length > 0 && (
                    <div className="flex flex-wrap gap-2 mt-4">
                      {user.skills.map((skill, index) => (
                        <Badge key={index} variant="secondary">{skill}</Badge>
                      ))}
                    </div>
                  )}
                </div>
              </div>

              {showResume && user.resume && (
                <div className="mt-6 pt-6 border-t">
                  <h3 className="font-semibold mb-2">О себе</h3>
                  <p className="text-gray-700 whitespace-pre-wrap">{user.resume}</p>
                </div>
              )}
            </CardContent>
          </Card>

          {showApplications && applications.length > 0 && (
            <Card className="mb-6">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Briefcase className="w-5 h-5" />
                  Отклики
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  {applications.slice(0, 5).map((app) => {
                    const opportunity = opportunities.find(o => o.id === app.opportunityId);
                    const company = opportunity ? companies.find(c => c.id === opportunity.companyId) : null;
                    return (
                      <div 
                        key={app.id} 
                        className="border rounded-lg p-4 cursor-pointer hover:bg-gray-50"
                        onClick={() => navigate(`/opportunity/${app.opportunityId}?from=user&userId=${id}`)}
                      >
                        <h4 className="font-semibold">{opportunity?.title || 'Возможность'}</h4>
                        {company && <p className="text-sm text-gray-500">{company.name}</p>}
                      </div>
                    );
                  })}
                </div>
              </CardContent>
            </Card>
          )}

          {isFriend && favorites.length > 0 && (
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Star className="w-5 h-5" />
                  Избранное
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  {favorites.map((opp) => {
                    const company = companies.find(c => c.id === opp.companyId);
                    return (
                      <div 
                        key={opp.id} 
                        className="border rounded-lg p-4 cursor-pointer hover:bg-gray-50"
                        onClick={() => navigate(`/opportunity/${opp.id}`)}
                      >
                        <h4 className="font-semibold">{opp.title}</h4>
                        <p className="text-sm text-gray-500">{company?.name}</p>
                      </div>
                    );
                  })}
                </div>
              </CardContent>
            </Card>
          )}
        </div>
      </main>
    </div>
  );
};