import React, { useState } from 'react';
import { useNavigate } from 'react-router';
import { useAuth } from '../context/AuthContext';
import { Button } from '../components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Textarea } from '../components/ui/textarea';
import { Badge } from '../components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../components/ui/tabs';
import { Switch } from '../components/ui/switch';
import { Avatar, AvatarFallback } from '../components/ui/avatar';
import { toast } from 'sonner';
import {
  LogOut,
  Home,
  User,
  Briefcase,
  Star,
  Users,
  Settings,
  GraduationCap,
  Link as LinkIcon,
  CheckCircle,
  Clock,
  XCircle,
} from 'lucide-react';
import { applications, opportunities, companies } from '../data/mockData';

export const ApplicantDashboard: React.FC = () => {
  const navigate = useNavigate();
  const { currentUser, logout } = useAuth();
  const [activeTab, setActiveTab] = useState('profile');

  // Профиль соискателя
  const [profile, setProfile] = useState({
    fullName: currentUser?.fullName || '',
    university: currentUser?.university || '',
    course: currentUser?.course || '',
    skills: currentUser?.skills || [],
    portfolioLinks: currentUser?.portfolioLinks || [],
    resume: currentUser?.resume || '',
  });

  const [newSkill, setNewSkill] = useState('');
  const [newLink, setNewLink] = useState('');

  // Настройки приватности
  const [privacySettings, setPrivacySettings] = useState({
    showApplications: currentUser?.privacySettings?.showApplications || false,
    showResume: currentUser?.privacySettings?.showResume || true,
  });

  // Контакты
  const [contacts, setContacts] = useState<string[]>(currentUser?.contacts || []);

  // Избранное из localStorage
  const [favorites] = useState<string[]>(() => {
    const saved = localStorage.getItem('favorites');
    return saved ? JSON.parse(saved) : [];
  });

  const handleLogout = () => {
    logout();
    toast.success('Вы вышли из системы');
    navigate('/');
  };

  const handleSaveProfile = () => {
    toast.success('Профиль сохранен!');
  };

  const addSkill = () => {
    if (newSkill && !profile.skills.includes(newSkill)) {
      setProfile({ ...profile, skills: [...profile.skills, newSkill] });
      setNewSkill('');
    }
  };

  const removeSkill = (skill: string) => {
    setProfile({ ...profile, skills: profile.skills.filter((s) => s !== skill) });
  };

  const addLink = () => {
    if (newLink && !profile.portfolioLinks.includes(newLink)) {
      setProfile({ ...profile, portfolioLinks: [...profile.portfolioLinks, newLink] });
      setNewLink('');
    }
  };

  const removeLink = (link: string) => {
    setProfile({ ...profile, portfolioLinks: profile.portfolioLinks.filter((l) => l !== link) });
  };

  // Получаем отклики текущего пользователя
  const userApplications = applications.filter((app) => app.applicantId === currentUser?.id);

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'accepted':
        return <CheckCircle className="w-4 h-4 text-green-500" />;
      case 'rejected':
        return <XCircle className="w-4 h-4 text-red-500" />;
      case 'pending':
        return <Clock className="w-4 h-4 text-yellow-500" />;
      default:
        return <Clock className="w-4 h-4 text-gray-500" />;
    }
  };

  const getStatusLabel = (status: string) => {
    switch (status) {
      case 'accepted':
        return 'Принят';
      case 'rejected':
        return 'Отклонен';
      case 'reserved':
        return 'В резерве';
      default:
        return 'На рассмотрении';
    }
  };

  if (!currentUser || currentUser.role !== 'applicant') {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Card>
          <CardHeader>
            <CardTitle>Доступ запрещен</CardTitle>
            <CardDescription>Эта страница доступна только для соискателей</CardDescription>
          </CardHeader>
          <CardContent>
            <Button onClick={() => navigate('/')}>На главную</Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white border-b">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-purple-600 rounded-lg flex items-center justify-center">
                <span className="text-white font-bold text-xl">Т</span>
              </div>
              <h1 className="text-xl font-bold">Личный кабинет соискателя</h1>
            </div>

            <div className="flex items-center gap-3">
              <Button variant="outline" onClick={() => navigate('/')}>
                <Home className="w-4 h-4 mr-2" />
                На главную
              </Button>
              <Button variant="outline" onClick={handleLogout}>
                <LogOut className="w-4 h-4 mr-2" />
                Выйти
              </Button>
            </div>
          </div>
        </div>
      </header>

      {/* Content */}
      <div className="container mx-auto px-4 py-6">
        <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
          {/* Sidebar */}
          <div className="lg:col-span-1">
            <Card>
              <CardContent className="pt-6">
                <div className="flex flex-col items-center text-center">
                  <Avatar className="w-20 h-20 mb-3">
                    <AvatarFallback className="text-2xl bg-gradient-to-br from-blue-500 to-purple-600 text-white">
                      {currentUser.displayName.charAt(0)}
                    </AvatarFallback>
                  </Avatar>
                  <h3 className="font-semibold text-lg">{currentUser.displayName}</h3>
                  <p className="text-sm text-gray-500">{currentUser.email}</p>
                </div>

                <div className="mt-6 space-y-2">
                  <Button
                    variant={activeTab === 'profile' ? 'default' : 'ghost'}
                    className="w-full justify-start"
                    onClick={() => setActiveTab('profile')}
                  >
                    <User className="w-4 h-4 mr-2" />
                    Профиль
                  </Button>
                  <Button
                    variant={activeTab === 'applications' ? 'default' : 'ghost'}
                    className="w-full justify-start"
                    onClick={() => setActiveTab('applications')}
                  >
                    <Briefcase className="w-4 h-4 mr-2" />
                    Мои отклики
                  </Button>
                  <Button
                    variant={activeTab === 'favorites' ? 'default' : 'ghost'}
                    className="w-full justify-start"
                    onClick={() => setActiveTab('favorites')}
                  >
                    <Star className="w-4 h-4 mr-2" />
                    Избранное
                  </Button>
                  <Button
                    variant={activeTab === 'contacts' ? 'default' : 'ghost'}
                    className="w-full justify-start"
                    onClick={() => setActiveTab('contacts')}
                  >
                    <Users className="w-4 h-4 mr-2" />
                    Контакты
                  </Button>
                  <Button
                    variant={activeTab === 'settings' ? 'default' : 'ghost'}
                    className="w-full justify-start"
                    onClick={() => setActiveTab('settings')}
                  >
                    <Settings className="w-4 h-4 mr-2" />
                    Настройки
                  </Button>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Main Content */}
          <div className="lg:col-span-3">
            {activeTab === 'profile' && (
              <Card>
                <CardHeader>
                  <CardTitle>Мой профиль</CardTitle>
                  <CardDescription>Управление вашими данными и резюме</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label htmlFor="fullName">ФИО</Label>
                      <Input
                        id="fullName"
                        value={profile.fullName}
                        onChange={(e) => setProfile({ ...profile, fullName: e.target.value })}
                        placeholder="Иванов Иван Иванович"
                      />
                    </div>

                    <div className="space-y-2">
                      <Label htmlFor="university">
                        <GraduationCap className="w-4 h-4 inline mr-1" />
                        Университет
                      </Label>
                      <Input
                        id="university"
                        value={profile.university}
                        onChange={(e) => setProfile({ ...profile, university: e.target.value })}
                        placeholder="МГУ им. М.В. Ломоносова"
                      />
                    </div>

                    <div className="space-y-2">
                      <Label htmlFor="course">Курс / Год выпуска</Label>
                      <Input
                        id="course"
                        value={profile.course}
                        onChange={(e) => setProfile({ ...profile, course: e.target.value })}
                        placeholder="3 курс / 2024"
                      />
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label>Навыки</Label>
                    <div className="flex gap-2">
                      <Input
                        value={newSkill}
                        onChange={(e) => setNewSkill(e.target.value)}
                        placeholder="Добавить навык"
                        onKeyDown={(e) => e.key === 'Enter' && (e.preventDefault(), addSkill())}
                      />
                      <Button onClick={addSkill}>Добавить</Button>
                    </div>
                    <div className="flex flex-wrap gap-2 mt-2">
                      {profile.skills.map((skill) => (
                        <Badge key={skill} variant="secondary">
                          {skill}
                          <button onClick={() => removeSkill(skill)} className="ml-2">
                            ×
                          </button>
                        </Badge>
                      ))}
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label>
                      <LinkIcon className="w-4 h-4 inline mr-1" />
                      Портфолио и ссылки
                    </Label>
                    <div className="flex gap-2">
                      <Input
                        value={newLink}
                        onChange={(e) => setNewLink(e.target.value)}
                        placeholder="https://github.com/username"
                        onKeyDown={(e) => e.key === 'Enter' && (e.preventDefault(), addLink())}
                      />
                      <Button onClick={addLink}>Добавить</Button>
                    </div>
                    <div className="space-y-1 mt-2">
                      {profile.portfolioLinks.map((link) => (
                        <div key={link} className="flex items-center justify-between bg-gray-50 p-2 rounded">
                          <a href={link} target="_blank" rel="noopener noreferrer" className="text-sm text-blue-600 hover:underline truncate">
                            {link}
                          </a>
                          <Button variant="ghost" size="sm" onClick={() => removeLink(link)}>
                            ×
                          </Button>
                        </div>
                      ))}
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="resume">О себе / Резюме</Label>
                    <Textarea
                      id="resume"
                      value={profile.resume}
                      onChange={(e) => setProfile({ ...profile, resume: e.target.value })}
                      placeholder="Расскажите о себе, опыте работы над проектами..."
                      rows={5}
                    />
                  </div>

                  <Button onClick={handleSaveProfile}>Сохранить изменения</Button>
                </CardContent>
              </Card>
            )}

            {activeTab === 'applications' && (
              <Card>
                <CardHeader>
                  <CardTitle>Мои отклики</CardTitle>
                  <CardDescription>История откликов на вакансии и мероприятия</CardDescription>
                </CardHeader>
                <CardContent>
                  {userApplications.length === 0 ? (
                    <div className="text-center py-8 text-gray-500">
                      <Briefcase className="w-12 h-12 mx-auto mb-3 text-gray-300" />
                      <p>У вас пока нет откликов</p>
                      <Button variant="outline" className="mt-4" onClick={() => navigate('/')}>
                        Найти вакансии
                      </Button>
                    </div>
                  ) : (
                    <div className="space-y-3">
                      {userApplications.map((app) => {
                        const opportunity = opportunities.find((o) => o.id === app.opportunityId);
                        const company = companies.find((c) => c.id === opportunity?.companyId);

                        return (
                          <div key={app.id} className="border rounded-lg p-4">
                            <div className="flex items-start justify-between mb-2">
                              <div>
                                <h4 className="font-semibold">{opportunity?.title}</h4>
                                <p className="text-sm text-gray-500">{company?.name}</p>
                              </div>
                              <div className="flex items-center gap-2">
                                {getStatusIcon(app.status)}
                                <Badge variant={app.status === 'accepted' ? 'default' : 'secondary'}>
                                  {getStatusLabel(app.status)}
                                </Badge>
                              </div>
                            </div>
                            <p className="text-sm text-gray-600 mb-2">{app.message}</p>
                            <p className="text-xs text-gray-400">
                              Откликнулись: {new Date(app.appliedDate).toLocaleDateString('ru-RU')}
                            </p>
                          </div>
                        );
                      })}
                    </div>
                  )}
                </CardContent>
              </Card>
            )}

            {activeTab === 'favorites' && (
              <Card>
                <CardHeader>
                  <CardTitle>Избранное</CardTitle>
                  <CardDescription>Вакансии и мероприятия, которые вас заинтересовали</CardDescription>
                </CardHeader>
                <CardContent>
                  {favorites.length === 0 ? (
                    <div className="text-center py-8 text-gray-500">
                      <Star className="w-12 h-12 mx-auto mb-3 text-gray-300" />
                      <p>У вас пока нет избранных возможностей</p>
                      <Button variant="outline" className="mt-4" onClick={() => navigate('/')}>
                        Найти вакансии
                      </Button>
                    </div>
                  ) : (
                    <div className="space-y-3">
                      {favorites.map((favId) => {
                        const opportunity = opportunities.find((o) => o.id === favId);
                        const company = companies.find((c) => c.id === opportunity?.companyId);

                        if (!opportunity) return null;

                        return (
                          <div key={favId} className="border rounded-lg p-4">
                            <h4 className="font-semibold mb-1">{opportunity.title}</h4>
                            <p className="text-sm text-gray-500 mb-2">{company?.name}</p>
                            <p className="text-sm text-gray-600 line-clamp-2 mb-2">{opportunity.description}</p>
                            <div className="flex gap-2">
                              <Badge variant="secondary">{opportunity.type}</Badge>
                              <Badge variant="outline">{opportunity.workFormat}</Badge>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  )}
                </CardContent>
              </Card>
            )}

            {activeTab === 'contacts' && (
              <Card>
                <CardHeader>
                  <CardTitle>Профессиональные контакты</CardTitle>
                  <CardDescription>Нетворкинг с другими соискателями</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="text-center py-8 text-gray-500">
                    <Users className="w-12 h-12 mx-auto mb-3 text-gray-300" />
                    <p>Функция находится в разработке</p>
                    <p className="text-sm mt-2">Скоро вы сможете добавлять контакты и обмениваться рекомендациями</p>
                  </div>
                </CardContent>
              </Card>
            )}

            {activeTab === 'settings' && (
              <Card>
                <CardHeader>
                  <CardTitle>Настройки приватности</CardTitle>
                  <CardDescription>Управление видимостью вашего профиля</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="flex items-center justify-between">
                    <div className="space-y-0.5">
                      <Label>Показывать отклики другим соискателям</Label>
                      <p className="text-sm text-gray-500">Другие пользователи смогут видеть на какие вакансии вы откликнулись</p>
                    </div>
                    <Switch
                      checked={privacySettings.showApplications}
                      onCheckedChange={(checked) =>
                        setPrivacySettings({ ...privacySettings, showApplications: checked })
                      }
                    />
                  </div>

                  <div className="flex items-center justify-between">
                    <div className="space-y-0.5">
                      <Label>Открыть резюме для всех</Label>
                      <p className="text-sm text-gray-500">Ваше резюме будет видно всем авторизованным пользователям</p>
                    </div>
                    <Switch
                      checked={privacySettings.showResume}
                      onCheckedChange={(checked) =>
                        setPrivacySettings({ ...privacySettings, showResume: checked })
                      }
                    />
                  </div>

                  <Button onClick={() => toast.success('Настройки сохранены')}>Сохранить настройки</Button>
                </CardContent>
              </Card>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
