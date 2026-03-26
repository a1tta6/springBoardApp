import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, useSearchParams, Link } from 'react-router';
import { Opportunity, Company, Tag } from '../types';
import { Button } from '../components/ui/button';
import { Badge } from '../components/ui/badge';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { appApi } from '../api/appApi';
import { useAuth } from '../context/AuthContext';
import { toast } from 'sonner';
import { 
  MapPin, Building2, Calendar, Banknote, Briefcase, Star, 
  ArrowLeft, ExternalLink, Mail, Phone, Globe, CheckCircle, XCircle, Clock 
} from 'lucide-react';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '../components/ui/dialog';
import { Textarea } from '../components/ui/textarea';

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
        const [oppData, nextCompanies, nextTags, applied] = await Promise.all([
          appApi.getOpportunity(id),
          appApi.getCompanies(),
          appApi.getTags(),
          currentUser?.role === 'applicant' ? appApi.getHasApplied(id) : Promise.resolve(false),
        ]);
        
        setOpportunity(oppData);
        setCompanies(nextCompanies);
        setTags(nextTags);
        setHasApplied(applied);
        
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
                <div className="pt-6 border-t">
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
                    </div>
                  ) : (
                    <Button
                      onClick={handleApply}
                      disabled={isApplying}
                      className="w-full sm:w-auto"
                      size="lg"
                    >
                      {isApplying ? 'Отправка...' : 'Откликнуться на вакансию'}
                    </Button>
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
                <CardTitle>О компании</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <div>
                    <h4 className="font-medium mb-1">{company.name}</h4>
                    {company.industry && (
                      <p className="text-gray-600 text-sm">Отрасль: {company.industry}</p>
                    )}
                  </div>
                  
                  {company.description && (
                    <p className="text-gray-700">{company.description}</p>
                  )}

                  <div className="flex flex-wrap gap-3">
                    {company.website && (
                      <a
                        href={company.website}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="flex items-center gap-2 text-blue-600 hover:text-blue-800"
                      >
                        <Globe className="w-4 h-4" />
                        Сайт компании
                        <ExternalLink className="w-3 h-3" />
                      </a>
                    )}
                    {company.socialLinks?.linkedin && (
                      <a
                        href={company.socialLinks.linkedin}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-blue-600 hover:text-blue-800"
                      >
                        LinkedIn
                      </a>
                    )}
                    {company.socialLinks?.vk && (
                      <a
                        href={company.socialLinks.vk}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-blue-600 hover:text-blue-800"
                      >
                        ВКонтакте
                      </a>
                    )}
                    {company.socialLinks?.telegram && (
                      <a
                        href={`https://t.me/${company.socialLinks.telegram.replace('@', '')}`}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-blue-600 hover:text-blue-800"
                      >
                        Telegram
                      </a>
                    )}
                  </div>
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
        </div>
      </main>
    </div>
  );
};
