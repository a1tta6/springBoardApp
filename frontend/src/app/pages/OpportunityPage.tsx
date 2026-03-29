import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, useSearchParams, Link } from 'react-router';
import { Opportunity, Company, Tag } from '../types';
import { Button } from '../components/ui/button';
import { Badge } from '../components/ui/badge';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { appApi, Friend } from '../api/appApi';
import { useAuth } from '../context/AuthContext';
import { toast } from 'sonner';
import { 
  MapPin, Building2, Calendar, Banknote, Briefcase, Star, 
  ArrowLeft, ExternalLink, Mail, Phone, Globe, CheckCircle, XCircle, Clock 
} from 'lucide-react';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '../components/ui/dialog';
import { Textarea } from '../components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../components/ui/select';
import { Label } from '../components/ui/label';

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

const statusLabels: Record<string, string> = {
  pending: 'В обработке',
  accepted: 'Принят',
  rejected: 'Отклонен',
  reserved: 'В резерве',
};

const statusColors: Record<string, string> = {
  pending: 'bg-yellow-100 text-yellow-800',
  accepted: 'bg-green-100 text-green-800',
  rejected: 'bg-red-100 text-red-800',
  reserved: 'bg-blue-100 text-blue-800',
};

export const OpportunityPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { currentUser, isReady } = useAuth();
  
  const [opportunity, setOpportunity] = useState<Opportunity | null>(null);
  const [companies, setCompanies] = useState<Company[]>([]);
  const [tags, setTags] = useState<Tag[]>([]);
  const [hasApplied, setHasApplied] = useState(false);
  const [isFavorite, setIsFavorite] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [isApplying, setIsApplying] = useState(false);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [coverLetter, setCoverLetter] = useState('');

  const [friends, setFriends] = useState<Friend[]>([]);
  const [isRecommendDialogOpen, setIsRecommendDialogOpen] = useState(false);
  const [selectedFriendId, setSelectedFriendId] = useState('');
  const [recommendComment, setRecommendComment] = useState('');
  const [isRecommending, setIsRecommending] = useState(false);

  const returnTo = searchParams.get('from');
  const userId = searchParams.get('userId');
  
  const handleGoBack = () => {
    if (returnTo === 'applications' || returnTo === 'favorites') {
      navigate(`/dashboard/applicant?tab=${returnTo}`);
    } else if (returnTo === 'user' && userId) {
      navigate(`/user/${userId}`);
    } else {
      navigate(-1);
    }
  };

  useEffect(() => {
    async function load() {
      if (!id) return;
      
      try {
        const [oppData, nextCompanies, nextTags, applied, myFriends] = await Promise.all([
          appApi.getOpportunity(id),
          appApi.getCompanies(),
          appApi.getTags(),
          currentUser?.role === 'applicant' ? appApi.getHasApplied(id) : Promise.resolve(false),
          currentUser?.role === 'applicant' ? appApi.getFriends() : Promise.resolve([]),
        ]);
        
        setOpportunity(oppData);
        setCompanies(nextCompanies);
        setTags(nextTags);
        setHasApplied(applied);
        setFriends(myFriends);
        
        if (currentUser?.role === 'applicant') {
          const favorites = await appApi.getFavorites();
          setIsFavorite(favorites.some(f => f.id === id));
        }
      } catch (error) {
        toast.error('Не удалось загрузить информацию о возможности');
        navigate('/');
      } finally {
        setIsLoading(false);
      }
    }

    if (isReady) {
      void load();
    }
  }, [id, currentUser, isReady, navigate]);

  const handleApply = async () => {
    if (!currentUser) {
      toast.error('Необходимо войти в систему');
      navigate('/login');
      return;
    }

    if (opportunity && (opportunity.type === 'internship' || opportunity.type === 'vacancy')) {
      setIsDialogOpen(true);
    } else {
      setIsApplying(true);
      try {
        await appApi.applyToOpportunity(id!);
        setHasApplied(true);
        toast.success('Отклик успешно отправлен!');
      } catch (error) {
        toast.error(error instanceof Error ? error.message : 'Не удалось отправить отклик');
      } finally {
        setIsApplying(false);
      }
    }
  };

  const handleSubmitApplication = async () => {
    setIsApplying(true);
    try {
      await appApi.applyToOpportunity(id!, coverLetter);
      setHasApplied(true);
      setIsDialogOpen(false);
      setCoverLetter('');
      toast.success('Отклик успешно отправлен!');
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Не удалось отправить отклик');
    } finally {
      setIsApplying(false);
    }
  };

  const handleCancelApplication = async () => {
    setIsApplying(true);
    try {
      await appApi.cancelApplication(id!);
      setHasApplied(false);
      toast.success('Отклик отменен');
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Не удалось отменить отклик');
    } finally {
      setIsApplying(false);
    }
  };

  const handleToggleFavorite = async () => {
    try {
      if (isFavorite) {
        await appApi.removeFavorite(id!);
        setIsFavorite(false);
      } else {
        await appApi.addFavorite(id!);
        setIsFavorite(true);
      }
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Не удалось обновить избранное');
    }
  };

  const handleRecommendOpportunity = async () => {
    if (!selectedFriendId) return;
    setIsRecommending(true);
    try {
      await appApi.recommendOpportunityToFriend({
        friendId: selectedFriendId,
        opportunityId: id!,
        comment: recommendComment
      });
      toast.success('Рекомендация отправлена');
      setIsRecommendDialogOpen(false);
      setSelectedFriendId('');
      setRecommendComment('');
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Не удалось отправить рекомендацию');
    } finally {
      setIsRecommending(false);
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-gray-500">Загрузка...</div>
      </div>
    );
  }

  if (!opportunity) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-gray-500">Возможность не найдена</div>
      </div>
    );
  }

  const company = companies.find(c => c.id === opportunity.companyId);
  const opportunityTags = tags.filter(tag => opportunity.tags.includes(tag.id));

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
          <div className="mb-6">
            <Button variant="ghost" onClick={handleGoBack} className="mb-4">
              <ArrowLeft className="w-4 h-4 mr-2" />
              Вернуться к списку
            </Button>
          </div>

          <Card className="mb-6">
            <CardHeader>
              <div className="flex justify-between items-start">
                <div className="flex-1">
                  <div className="flex items-center gap-3 mb-2">
                    <Badge variant="default" className="text-sm">
                      {opportunityTypeLabels[opportunity.type]}
                    </Badge>
                    <Badge variant="outline">
                      {workFormatLabels[opportunity.workFormat]}
                    </Badge>
                    {hasApplied && (
                      <Badge className="bg-green-100 text-green-800 border-green-300">
                        <CheckCircle className="w-3 h-3 mr-1" />
                        Вы откликнулись
                      </Badge>
                    )}
                  </div>
                  <CardTitle className="text-3xl mb-2">{opportunity.title}</CardTitle>
                  {company && (
                    <Link 
                      to={`/company/${company.id}`}
                      className="flex items-center gap-2 text-gray-600 hover:text-blue-600 transition-colors"
                    >
                      <Building2 className="w-5 h-5" />
                      <span className="text-lg">{company.name}</span>
                      {company.verified && (
                        <Badge variant="secondary" className="text-xs">Верифицирован</Badge>
                      )}
                    </Link>
                  )}
                </div>
                {currentUser?.role === 'applicant' && (
                  <Button
                    variant="ghost"
                    size="icon"
                    onClick={handleToggleFavorite}
                    className={isFavorite ? 'text-yellow-500' : 'text-gray-400'}
                  >
                    <Star className={`w-6 h-6 ${isFavorite ? 'fill-current' : ''}`} />
                  </Button>
                )}
              </div>
            </CardHeader>

            <CardContent className="space-y-6">
              <div>
                <h3 className="text-lg font-semibold mb-2">Описание</h3>
                <p className="text-gray-700 whitespace-pre-wrap">{opportunity.description}</p>
              </div>

              {opportunity.requirements && (
                <div>
                  <h3 className="text-lg font-semibold mb-2">Требования</h3>
                  <p className="text-gray-700 whitespace-pre-wrap">{opportunity.requirements}</p>
                </div>
              )}

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-4">
                  <div className="flex items-center gap-3">
                    <div className="p-2 bg-gray-100 rounded-lg">
                      <MapPin className="w-5 h-5 text-gray-600" />
                    </div>
                    <div>
                      <div className="text-sm text-gray-500">Местоположение</div>
                      <div className="font-medium">
                        {opportunity.location.address 
                          ? `${opportunity.location.address}, ${opportunity.location.city}`
                          : opportunity.location.city}
                      </div>
                    </div>
                  </div>

                  {opportunity.salary && (
                    <div className="flex items-center gap-3">
                      <div className="p-2 bg-gray-100 rounded-lg">
                        <Banknote className="w-5 h-5 text-gray-600" />
                      </div>
                      <div>
                        <div className="text-sm text-gray-500">Зарплата</div>
                        <div className="font-medium">
                          {opportunity.salary.min?.toLocaleString()} - {opportunity.salary.max?.toLocaleString()} {opportunity.salary.currency}
                        </div>
                      </div>
                    </div>
                  )}

                  {opportunity.eventDate && (
                    <div className="flex items-center gap-3">
                      <div className="p-2 bg-gray-100 rounded-lg">
                        <Calendar className="w-5 h-5 text-gray-600" />
                      </div>
                      <div>
                        <div className="text-sm text-gray-500">Дата мероприятия</div>
                        <div className="font-medium">
                          {new Date(opportunity.eventDate).toLocaleDateString('ru-RU', {
                            weekday: 'long',
                            year: 'numeric',
                            month: 'long',
                            day: 'numeric'
                          })}
                        </div>
                      </div>
                    </div>
                  )}

                  <div className="flex items-center gap-3">
                    <div className="p-2 bg-gray-100 rounded-lg">
                      <Briefcase className="w-5 h-5 text-gray-600" />
                    </div>
                    <div>
                      <div className="text-sm text-gray-500">Опубликовано</div>
                      <div className="font-medium">
                        {new Date(opportunity.publishedDate).toLocaleDateString('ru-RU')}
                      </div>
                    </div>
                  </div>

                  {opportunity.expiryDate && (
                    <div className="flex items-center gap-3">
                      <div className="p-2 bg-gray-100 rounded-lg">
                        <Clock className="w-5 h-5 text-gray-600" />
                      </div>
                      <div>
                        <div className="text-sm text-gray-500">Срок действия</div>
                        <div className="font-medium">
                          до {new Date(opportunity.expiryDate).toLocaleDateString('ru-RU')}
                        </div>
                      </div>
                    </div>
                  )}
                </div>

                <div className="space-y-4">
                  <h3 className="font-semibold">Контактная информация</h3>
                  {opportunity.contactInfo.email && (
                    <a 
                      href={`mailto:${opportunity.contactInfo.email}`}
                      className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
                    >
                      <Mail className="w-5 h-5 text-gray-600" />
                      <span>{opportunity.contactInfo.email}</span>
                    </a>
                  )}
                  {opportunity.contactInfo.phone && (
                    <a 
                      href={`tel:${opportunity.contactInfo.phone}`}
                      className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
                    >
                      <Phone className="w-5 h-5 text-gray-600" />
                      <span>{opportunity.contactInfo.phone}</span>
                    </a>
                  )}
                  {opportunity.contactInfo.website && (
                    <a 
                      href={opportunity.contactInfo.website}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
                    >
                      <Globe className="w-5 h-5 text-gray-600" />
                      <span className="flex items-center gap-1">
                        {opportunity.contactInfo.website}
                        <ExternalLink className="w-3 h-3" />
                      </span>
                    </a>
                  )}
                </div>
              </div>

              {opportunityTags.length > 0 && (
                <div>
                  <h3 className="font-semibold mb-3">Теги</h3>
                  <div className="flex flex-wrap gap-2">
                    {opportunityTags.map((tag) => (
                      <Badge 
                        key={tag.id} 
                        variant={tag.category === 'technology' ? 'default' : 'secondary'}
                        className="text-sm"
                      >
                        {tag.name}
                      </Badge>
                    ))}
                  </div>
                </div>
              )}

              {currentUser?.role === 'applicant' && (
                <div className="pt-6 border-t flex flex-col gap-3">
                  {hasApplied ? (
                    <div className="flex flex-col sm:flex-row gap-3">
                      <Button
                        variant="outline"
                        onClick={handleCancelApplication}
                        disabled={isApplying}
                        className="flex-1"
                      >
                        <XCircle className="w-4 h-4 mr-2" />
                        {isApplying ? 'Отмена...' : 'Отменить отклик'}
                      </Button>
                      <Button
                        variant="outline"
                        onClick={() => navigate('/dashboard/applicant?tab=applications')}
                        className="flex-1"
                      >
                        Перейти в личный кабинет
                      </Button>
                      <Button
                        variant="outline"
                        onClick={() => setIsRecommendDialogOpen(true)}
                        className="flex-1"
                      >
                        <Star className="w-4 h-4 mr-2" />
                        Рекомендовать другу
                      </Button>
                    </div>
                  ) : (
                    <div className="flex flex-col sm:flex-row gap-3">
                      <Button
                        onClick={handleApply}
                        disabled={isApplying}
                        className="flex-1"
                        size="lg"
                      >
                        {isApplying ? 'Отправка...' : 'Откликнуться на вакансию'}
                      </Button>
                      <Button
                        variant="outline"
                        onClick={() => setIsRecommendDialogOpen(true)}
                        className="flex-1"
                        size="lg"
                      >
                        <Star className="w-4 h-4 mr-2" />
                        Рекомендовать другу
                      </Button>
                    </div>
                  )}
                </div>
              )}

              {!currentUser && (
                <div className="pt-6 border-t">
                  <p className="text-gray-600 mb-3">
                    Войдите в систему, чтобы откликнуться на эту вакансию
                  </p>
                  <div className="flex gap-3">
                    <Button onClick={() => navigate('/login')} className="flex-1">
                      Войти
                    </Button>
                    <Button variant="outline" onClick={() => navigate('/register')} className="flex-1">
                      Зарегистрироваться
                    </Button>
                  </div>
                </div>
              )}
            </CardContent>
          </Card>

          {company && (
            <Card>
              <CardHeader>
                <CardTitle>
                  <Link to={`/company/${company.id}`} className="hover:text-blue-600">
                    О компании
                  </Link>
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <div className="flex items-center gap-4">
                    {company.logo ? (
                      <img src={company.logo} alt={company.name} className="w-20 h-20 object-contain border rounded-lg" />
                    ) : (
                      <div className="w-20 h-20 bg-gray-200 rounded-lg flex items-center justify-center">
                        <Building2 className="w-10 h-10 text-gray-400" />
                      </div>
                    )}
                    <div>
                      <Link to={`/company/${company.id}`} className="font-medium hover:text-blue-600">
                        {company.name}
                      </Link>
                    </div>
                  </div>
                  
                  {(company.inn || company.ogrn) && (
                    <div className="text-sm text-gray-600 space-y-1">
                      {company.inn && <p>ИНН: {company.inn}</p>}
                      {company.ogrn && <p>ОГРН: {company.ogrn}</p>}
                    </div>
                  )}

                  {company.address && (
                    <p className="text-gray-700">{company.address}</p>
                  )}

                  {company.socialLinks && (
                    <div className="text-sm text-gray-600 whitespace-pre-wrap">{company.socialLinks}</div>
                  )}
                  {company.bio && (
                    <p className="text-sm text-gray-700 mt-2">{company.bio}</p>
                  )}
                </div>
              </CardContent>
            </Card>
          )}

          <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Сопроводительное письмо</DialogTitle>
                <DialogDescription>
                  Расскажите о себе и почему вас заинтересовала эта позиция
                </DialogDescription>
              </DialogHeader>
              <Textarea
                value={coverLetter}
                onChange={(e) => setCoverLetter(e.target.value)}
                placeholder="Опишите ваш опыт и мотивацию..."
                className="min-h-[150px]"
              />
              <DialogFooter>
                <Button variant="outline" onClick={() => {
                  setIsDialogOpen(false);
                  setCoverLetter('');
                }}>
                  Отмена
                </Button>
                <Button onClick={handleSubmitApplication} disabled={isApplying}>
                  {isApplying ? 'Отправка...' : 'Отправить'}
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>

          <Dialog open={isRecommendDialogOpen} onOpenChange={setIsRecommendDialogOpen}>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Рекомендовать возможность другу</DialogTitle>
                <DialogDescription>
                  Выберите друга, которому вы хотите порекомендовать эту возможность
                </DialogDescription>
              </DialogHeader>
              
              <div className="space-y-4 py-4">
                <div className="space-y-2">
                  <Label>Ваши друзья</Label>
                  <Select value={selectedFriendId} onValueChange={setSelectedFriendId}>
                    <SelectTrigger>
                      <SelectValue placeholder="Выберите друга" />
                    </SelectTrigger>
                    <SelectContent>
                      {friends.map(friend => (
                        <SelectItem key={friend.userId} value={friend.userId}>
                          {friend.displayName || friend.email}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
                
                <div className="space-y-2">
                  <Label>Комментарий (необязательно)</Label>
                  <Textarea
                    value={recommendComment}
                    onChange={(e) => setRecommendComment(e.target.value)}
                    placeholder="Пара слов о том, почему эта возможность может быть интересна..."
                    rows={3}
                  />
                </div>
              </div>
              
              <DialogFooter>
                <Button variant="outline" onClick={() => setIsRecommendDialogOpen(false)}>Отмена</Button>
                <Button 
                  onClick={handleRecommendOpportunity} 
                  disabled={!selectedFriendId || isRecommending}
                >
                  {isRecommending ? 'Отправка...' : 'Отправить рекомендацию'}
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>
      </main>
    </div>
  );
};
