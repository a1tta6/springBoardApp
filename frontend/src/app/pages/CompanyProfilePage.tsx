import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router';
import { CompanyProfile as CompanyProfileType, Opportunity, statusMap } from '../types';
import { Button } from '../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { Badge } from '../components/ui/badge';
import { appApi, Friend } from '../api/appApi';
import { toast } from 'sonner';
import { useAuth } from '../context/AuthContext';
import { ArrowLeft, Globe, MapPin, CheckCircle, Briefcase, Building2, UserPlus } from 'lucide-react';
import { Input } from '../components/ui/input';

const opportunityTypeLabels: Record<string, string> = {
  internship: 'Стажировка',
  vacancy: 'Вакансия',
  mentorship: 'Менторство',
  event: 'Мероприятие',
};

const workFormatLabels: Record<string, string> = {
  office: 'Офис',
  hybrid: 'Гибрид',
  remote: 'Удаленно',
};

export const CompanyProfilePage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { currentUser } = useAuth();
  const [profile, setProfile] = useState<CompanyProfileType | null>(null);
  const [friends, setFriends] = useState<Friend[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isRecommending, setIsRecommending] = useState(false);
  const [recommendFriendId, setRecommendFriendId] = useState('');
  const [recommendComment, setRecommendComment] = useState('');

  useEffect(() => {
    async function load() {
      if (!id) return;
      try {
        const data = await appApi.getCompanyProfile(id);
        setProfile(data);
        if (currentUser?.role === 'applicant') {
          const friendsData = await appApi.getFriends();
          setFriends(friendsData);
        }
      } catch (error) {
        toast.error('Не удалось загрузить профиль компании');
      } finally {
        setIsLoading(false);
      }
    }
    void load();
  }, [id]);

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <p className="text-gray-500">Загрузка...</p>
      </div>
    );
  }

  if (!profile) {
    return (
      <div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center">
        <p className="text-gray-500 mb-4">Компания не найдена</p>
        <Button onClick={() => navigate('/')}>На главную</Button>
      </div>
    );
  }

  const { company, opportunities } = profile;

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white border-b">
        <div className="container mx-auto px-4 py-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <Button variant="ghost" size="icon" onClick={() => navigate(-1)}>
              <ArrowLeft className="w-5 h-5" />
            </Button>
            <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-purple-600 rounded-lg flex items-center justify-center" onClick={() => navigate('/')}>
              <span className="text-white font-bold text-xl">T</span>
            </div>
            <h1 className="text-xl font-bold">Профиль компании</h1>
          </div>
        </div>
      </header>

      <div className="container mx-auto px-4 py-6">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-1">
            <Card>
              <CardHeader>
                <CardTitle>О компании</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex items-center gap-4">
                  {company.logo ? (
                    <img src={company.logo} alt={company.name} className="w-24 h-24 object-contain border rounded-lg" />
                  ) : (
                    <div className="w-24 h-24 bg-gray-200 rounded-lg flex items-center justify-center">
                      <Building2 className="w-12 h-12 text-gray-400" />
                    </div>
                  )}
                    <div>
                    <h2 className="text-xl font-semibold">{company.name}</h2>
                    {company.verified && (
                      <div className="flex items-center gap-1 text-green-600 text-sm mt-1">
                        <CheckCircle className="w-4 h-4" />
                        Верифицирована
                      </div>
                    )}
                    {company.bio && (
                      <p className="text-sm text-gray-700 mt-2">{company.bio}</p>
                    )}
                  </div>
                </div>

                {(company.inn || company.ogrn) && (
                  <div className="text-sm text-gray-600 space-y-1">
                    {company.inn && <p>ИНН: {company.inn}</p>}
                    {company.ogrn && <p>ОГРН: {company.ogrn}</p>}
                  </div>
                )}

                {company.address && (
                  <div className="flex items-start gap-2 text-gray-600">
                    <MapPin className="w-4 h-4 mt-1" />
                    <span>{company.address}</span>
                  </div>
                )}

                {company.website && (
                  <a
                    href={company.website}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="flex items-center gap-2 text-blue-600 hover:text-blue-800"
                  >
                    <Globe className="w-4 h-4" />
                    Сайт компании
                  </a>
                )}

                {company.socialLinks && (
                  <div className="text-sm text-gray-600 whitespace-pre-wrap pt-2 border-t">
                    {company.socialLinks}
                  </div>
                )}
                
                {currentUser?.role === 'applicant' && (
                  <div className="pt-4 border-t mt-4">
                    {isRecommending ? (
                      <div className="space-y-3 bg-gray-50 p-3 rounded-lg">
                        <div className="font-semibold text-sm">Порекомендовать работодателя</div>
                        {friends.length === 0 ? (
                          <p className="text-sm text-gray-500">У вас пока нет друзей.</p>
                        ) : (
                          <>
                            <select 
                                className="w-full border p-2 rounded text-sm"
                                value={recommendFriendId}
                                onChange={e => setRecommendFriendId(e.target.value)}
                            >
                                <option value="">Выберите друга...</option>
                                {friends.map(f => (
                                    <option key={f.userId} value={f.userId}>{f.displayName} ({f.email})</option>
                                ))}
                            </select>
                            <Input 
                                placeholder="Комментарий (необязательно)" 
                                value={recommendComment} 
                                onChange={e => setRecommendComment(e.target.value)} 
                            />
                            <div className="flex gap-2">
                                <Button 
                                    size="sm" 
                                    disabled={!recommendFriendId}
                                    onClick={() => {
                                        appApi.recommendEmployerToFriend({
                                            friendId: recommendFriendId,
                                            companyId: company.id,
                                            comment: recommendComment
                                        }).then(() => {
                                            toast.success('Рекомендация отправлена');
                                            setIsRecommending(false);
                                        }).catch(err => {
                                            toast.error(err instanceof Error ? err.message : 'Ошибка при отправке');
                                        });
                                    }}
                                >Отправить</Button>
                                <Button size="sm" variant="outline" onClick={() => setIsRecommending(false)}>Отмена</Button>
                            </div>
                          </>
                        )}
                      </div>
                    ) : (
                      <Button variant="outline" className="w-full" onClick={() => {
                        setIsRecommending(true);
                        setRecommendFriendId('');
                        setRecommendComment('');
                      }}>
                        <UserPlus className="w-4 h-4 mr-2" />
                        Рекомендовать другу
                      </Button>
                    )}
                  </div>
                )}
              </CardContent>
            </Card>
          </div>

          <div className="lg:col-span-2">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Briefcase className="w-5 h-5" />
                  Возможности ({opportunities.length})
                </CardTitle>
              </CardHeader>
              <CardContent>
                {opportunities.length === 0 ? (
                  <p className="text-gray-500">Нет активных возможностей</p>
                ) : (
                  <div className="space-y-4">
                    {opportunities.map((opp) => (
                      <Link
                        key={opp.id}
                        to={`/opportunity/${opp.id}`}
                        className="block border rounded-lg p-4 hover:shadow-md transition-shadow"
                      >
                        <div className="flex justify-between items-start">
                          <div>
                            <h3 className="font-semibold text-lg">{opp.title}</h3>
                            <div className="flex items-center gap-2 text-sm text-gray-500 mt-1">
                              <span>{opportunityTypeLabels[opp.type] || opp.type}</span>
                              <span>•</span>
                              <span>{workFormatLabels[opp.workFormat] || opp.workFormat}</span>
                              <span>•</span>
                              <span>{opp.location.city}</span>
                            </div>
                            <p className="text-sm text-gray-600 mt-2 line-clamp-2">{opp.description}</p>
                          </div>
                          <Badge variant={opp.status === 'active' ? 'default' : 'secondary'}>
                            {opp.status === 'active' ? 'Активно' : statusMap[opp.status as keyof typeof statusMap] || opp.status}
                          </Badge>
                        </div>
                      </Link>
                    ))}
                  </div>
                )}
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
};
