import React, { useState } from 'react';
import { useNavigate } from 'react-router';
import { useAuth } from '../context/AuthContext';
import { Button } from '../components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Textarea } from '../components/ui/textarea';
import { Badge } from '../components/ui/badge';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../components/ui/select';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from '../components/ui/dialog';
import { Avatar, AvatarFallback } from '../components/ui/avatar';
import { toast } from 'sonner';
import {
  LogOut,
  Home,
  Building2,
  Plus,
  Briefcase,
  Users,
  Settings,
  CheckCircle,
  Clock,
  XCircle,
  Eye,
} from 'lucide-react';
import { companies, opportunities, applications, users } from '../data/mockData';
import { Opportunity, OpportunityType, WorkFormat } from '../types';

export const EmployerDashboard: React.FC = () => {
  const navigate = useNavigate();
  const { currentUser, logout } = useAuth();
  const [activeTab, setActiveTab] = useState('company');
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);

  // Получаем компанию работодателя
  const company = companies.find((c) => c.id === currentUser?.companyId);

  // Получаем вакансии компании
  const companyOpportunities = opportunities.filter((o) => o.companyId === currentUser?.companyId);

  // Получаем отклики на вакансии компании
  const companyApplications = applications.filter((app) =>
    companyOpportunities.some((opp) => opp.id === app.opportunityId)
  );

  // Новая возможность
  const [newOpportunity, setNewOpportunity] = useState<Partial<Opportunity>>({
    title: '',
    description: '',
    type: 'vacancy',
    workFormat: 'office',
    location: {
      city: '',
      address: '',
      coordinates: [55.751244, 37.618423],
    },
    salary: {
      min: 0,
      max: 0,
      currency: 'RUB',
    },
    contactInfo: {
      email: company?.email || '',
    },
    tags: [],
    status: 'active',
    requirements: '',
  });

  const handleLogout = () => {
    logout();
    toast.success('Вы вышли из системы');
    navigate('/');
  };

  const handleCreateOpportunity = () => {
    if (!newOpportunity.title || !newOpportunity.description) {
      toast.error('Заполните обязательные поля');
      return;
    }

    toast.success('Вакансия создана! Ожидает модерации.');
    setIsCreateDialogOpen(false);
    // Сброс формы
    setNewOpportunity({
      title: '',
      description: '',
      type: 'vacancy',
      workFormat: 'office',
      location: {
        city: '',
        address: '',
        coordinates: [55.751244, 37.618423],
      },
      salary: {
        min: 0,
        max: 0,
        currency: 'RUB',
      },
      contactInfo: {
        email: company?.email || '',
      },
      tags: [],
      status: 'active',
      requirements: '',
    });
  };

  const handleUpdateApplicationStatus = (appId: string, status: string) => {
    const app = applications.find((a) => a.id === appId);
    if (app) {
      app.status = status as any;
      toast.success(`Статус отклика изменен на "${status}"`);
    }
  };

  if (!currentUser || currentUser.role !== 'employer') {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Card>
          <CardHeader>
            <CardTitle>Доступ запрещен</CardTitle>
            <CardDescription>Эта страница доступна только для работодателей</CardDescription>
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
              <h1 className="text-xl font-bold">Личный кабинет работодателя</h1>
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
                      {company?.name.charAt(0) || 'К'}
                    </AvatarFallback>
                  </Avatar>
                  <h3 className="font-semibold text-lg">{company?.name || currentUser.displayName}</h3>
                  <p className="text-sm text-gray-500">{currentUser.email}</p>
                  {company?.verified && (
                    <Badge variant="default" className="mt-2">
                      <CheckCircle className="w-3 h-3 mr-1" />
                      Верифицирована
                    </Badge>
                  )}
                  {!company?.verified && (
                    <Badge variant="secondary" className="mt-2">
                      <Clock className="w-3 h-3 mr-1" />
                      Ожидает верификации
                    </Badge>
                  )}
                </div>

                <div className="mt-6 space-y-2">
                  <Button
                    variant={activeTab === 'company' ? 'default' : 'ghost'}
                    className="w-full justify-start"
                    onClick={() => setActiveTab('company')}
                  >
                    <Building2 className="w-4 h-4 mr-2" />
                    О компании
                  </Button>
                  <Button
                    variant={activeTab === 'opportunities' ? 'default' : 'ghost'}
                    className="w-full justify-start"
                    onClick={() => setActiveTab('opportunities')}
                  >
                    <Briefcase className="w-4 h-4 mr-2" />
                    Вакансии
                  </Button>
                  <Button
                    variant={activeTab === 'applications' ? 'default' : 'ghost'}
                    className="w-full justify-start"
                    onClick={() => setActiveTab('applications')}
                  >
                    <Users className="w-4 h-4 mr-2" />
                    Отклики
                  </Button>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Main Content */}
          <div className="lg:col-span-3">
            {activeTab === 'company' && (
              <Card>
                <CardHeader>
                  <CardTitle>Информация о компании</CardTitle>
                  <CardDescription>Управление данными вашей компании</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  {!company && (
                    <div className="bg-amber-50 border border-amber-200 rounded-lg p-4 mb-4">
                      <p className="text-amber-800">
                        Компания еще не создана. Обратитесь к куратору платформы для верификации.
                      </p>
                    </div>
                  )}

                  {company && (
                    <>
                      <div className="space-y-2">
                        <Label>Название компании</Label>
                        <Input value={company.name} disabled />
                      </div>

                      <div className="space-y-2">
                        <Label>Сфера деятельности</Label>
                        <Input value={company.industry} disabled />
                      </div>

                      <div className="space-y-2">
                        <Label>Описание</Label>
                        <Textarea value={company.description} disabled rows={4} />
                      </div>

                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div className="space-y-2">
                          <Label>Веб-сайт</Label>
                          <Input value={company.website || ''} disabled />
                        </div>

                        <div className="space-y-2">
                          <Label>Email</Label>
                          <Input value={company.email} disabled />
                        </div>
                      </div>

                      <div className="space-y-2">
                        <Label>Социальные сети</Label>
                        <div className="space-y-2">
                          {company.socialLinks?.linkedin && (
                            <Input value={company.socialLinks.linkedin} disabled />
                          )}
                          {company.socialLinks?.vk && <Input value={company.socialLinks.vk} disabled />}
                        </div>
                      </div>

                      <p className="text-sm text-gray-500 mt-4">
                        Для изменения информации о компании обратитесь к куратору платформы
                      </p>
                    </>
                  )}
                </CardContent>
              </Card>
            )}

            {activeTab === 'opportunities' && (
              <Card>
                <CardHeader>
                  <div className="flex justify-between items-center">
                    <div>
                      <CardTitle>Вакансии и мероприятия</CardTitle>
                      <CardDescription>Управление публикациями компании</CardDescription>
                    </div>
                    <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
                      <DialogTrigger asChild>
                        <Button disabled={!company?.verified}>
                          <Plus className="w-4 h-4 mr-2" />
                          Создать
                        </Button>
                      </DialogTrigger>
                      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
                        <DialogHeader>
                          <DialogTitle>Создать новую возможность</DialogTitle>
                          <DialogDescription>Заполните данные для публикации вакансии или мероприятия</DialogDescription>
                        </DialogHeader>
                        <div className="space-y-4">
                          <div className="space-y-2">
                            <Label>Тип</Label>
                            <Select
                              value={newOpportunity.type}
                              onValueChange={(value) => setNewOpportunity({ ...newOpportunity, type: value as OpportunityType })}
                            >
                              <SelectTrigger>
                                <SelectValue />
                              </SelectTrigger>
                              <SelectContent>
                                <SelectItem value="vacancy">Вакансия</SelectItem>
                                <SelectItem value="internship">Стажировка</SelectItem>
                                <SelectItem value="mentorship">Менторская программа</SelectItem>
                                <SelectItem value="event">Мероприятие</SelectItem>
                              </SelectContent>
                            </Select>
                          </div>

                          <div className="space-y-2">
                            <Label>Название</Label>
                            <Input
                              value={newOpportunity.title}
                              onChange={(e) => setNewOpportunity({ ...newOpportunity, title: e.target.value })}
                              placeholder="Junior Frontend Developer"
                            />
                          </div>

                          <div className="space-y-2">
                            <Label>Описание</Label>
                            <Textarea
                              value={newOpportunity.description}
                              onChange={(e) => setNewOpportunity({ ...newOpportunity, description: e.target.value })}
                              placeholder="Подробное описание вакансии..."
                              rows={4}
                            />
                          </div>

                          <div className="space-y-2">
                            <Label>Формат работы</Label>
                            <Select
                              value={newOpportunity.workFormat}
                              onValueChange={(value) => setNewOpportunity({ ...newOpportunity, workFormat: value as WorkFormat })}
                            >
                              <SelectTrigger>
                                <SelectValue />
                              </SelectTrigger>
                              <SelectContent>
                                <SelectItem value="office">Офис</SelectItem>
                                <SelectItem value="hybrid">Гибрид</SelectItem>
                                <SelectItem value="remote">Удаленно</SelectItem>
                              </SelectContent>
                            </Select>
                          </div>

                          <div className="grid grid-cols-2 gap-4">
                            <div className="space-y-2">
                              <Label>Город</Label>
                              <Input
                                value={newOpportunity.location?.city}
                                onChange={(e) =>
                                  setNewOpportunity({
                                    ...newOpportunity,
                                    location: { ...newOpportunity.location!, city: e.target.value },
                                  })
                                }
                                placeholder="Москва"
                              />
                            </div>

                            <div className="space-y-2">
                              <Label>Адрес (опционально)</Label>
                              <Input
                                value={newOpportunity.location?.address}
                                onChange={(e) =>
                                  setNewOpportunity({
                                    ...newOpportunity,
                                    location: { ...newOpportunity.location!, address: e.target.value },
                                  })
                                }
                                placeholder="ул. Ленина, 45"
                              />
                            </div>
                          </div>

                          {(newOpportunity.type === 'vacancy' || newOpportunity.type === 'internship') && (
                            <div className="grid grid-cols-2 gap-4">
                              <div className="space-y-2">
                                <Label>Зарплата от (₽)</Label>
                                <Input
                                  type="number"
                                  value={newOpportunity.salary?.min}
                                  onChange={(e) =>
                                    setNewOpportunity({
                                      ...newOpportunity,
                                      salary: { ...newOpportunity.salary!, min: parseInt(e.target.value) },
                                    })
                                  }
                                />
                              </div>

                              <div className="space-y-2">
                                <Label>Зарплата до (₽)</Label>
                                <Input
                                  type="number"
                                  value={newOpportunity.salary?.max}
                                  onChange={(e) =>
                                    setNewOpportunity({
                                      ...newOpportunity,
                                      salary: { ...newOpportunity.salary!, max: parseInt(e.target.value) },
                                    })
                                  }
                                />
                              </div>
                            </div>
                          )}

                          <div className="space-y-2">
                            <Label>Требования</Label>
                            <Textarea
                              value={newOpportunity.requirements}
                              onChange={(e) => setNewOpportunity({ ...newOpportunity, requirements: e.target.value })}
                              placeholder="Требования к кандидату..."
                              rows={3}
                            />
                          </div>

                          <div className="flex justify-end gap-2">
                            <Button variant="outline" onClick={() => setIsCreateDialogOpen(false)}>
                              Отмена
                            </Button>
                            <Button onClick={handleCreateOpportunity}>Создать</Button>
                          </div>
                        </div>
                      </DialogContent>
                    </Dialog>
                  </div>
                </CardHeader>
                <CardContent>
                  {!company?.verified && (
                    <div className="bg-amber-50 border border-amber-200 rounded-lg p-4 mb-4">
                      <p className="text-amber-800">
                        Для создания вакансий необходима верификация компании куратором платформы.
                      </p>
                    </div>
                  )}

                  {companyOpportunities.length === 0 ? (
                    <div className="text-center py-8 text-gray-500">
                      <Briefcase className="w-12 h-12 mx-auto mb-3 text-gray-300" />
                      <p>У вас пока нет опубликованных вакансий</p>
                    </div>
                  ) : (
                    <div className="space-y-3">
                      {companyOpportunities.map((opp) => (
                        <div key={opp.id} className="border rounded-lg p-4">
                          <div className="flex justify-between items-start mb-2">
                            <div>
                              <h4 className="font-semibold">{opp.title}</h4>
                              <p className="text-sm text-gray-500">{opp.location.city}</p>
                            </div>
                            <Badge variant={opp.status === 'active' ? 'default' : 'secondary'}>{opp.status}</Badge>
                          </div>
                          <p className="text-sm text-gray-600 mb-2 line-clamp-2">{opp.description}</p>
                          <div className="flex gap-2">
                            <Badge variant="outline">{opp.type}</Badge>
                            <Badge variant="outline">{opp.workFormat}</Badge>
                            {opp.salary && (
                              <Badge variant="secondary">
                                {opp.salary.min?.toLocaleString()} - {opp.salary.max?.toLocaleString()} ₽
                              </Badge>
                            )}
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </CardContent>
              </Card>
            )}

            {activeTab === 'applications' && (
              <Card>
                <CardHeader>
                  <CardTitle>Отклики соискателей</CardTitle>
                  <CardDescription>Управление откликами на ваши вакансии</CardDescription>
                </CardHeader>
                <CardContent>
                  {companyApplications.length === 0 ? (
                    <div className="text-center py-8 text-gray-500">
                      <Users className="w-12 h-12 mx-auto mb-3 text-gray-300" />
                      <p>Пока нет откликов на ваши вакансии</p>
                    </div>
                  ) : (
                    <div className="space-y-4">
                      {companyApplications.map((app) => {
                        const opportunity = opportunities.find((o) => o.id === app.opportunityId);
                        const applicant = users.find((u) => u.id === app.applicantId);

                        return (
                          <div key={app.id} className="border rounded-lg p-4">
                            <div className="flex justify-between items-start mb-3">
                              <div>
                                <h4 className="font-semibold">{applicant?.displayName}</h4>
                                <p className="text-sm text-gray-500">{opportunity?.title}</p>
                                <p className="text-xs text-gray-400 mt-1">
                                  {new Date(app.appliedDate).toLocaleDateString('ru-RU')}
                                </p>
                              </div>
                              <Badge
                                variant={
                                  app.status === 'accepted'
                                    ? 'default'
                                    : app.status === 'rejected'
                                    ? 'destructive'
                                    : 'secondary'
                                }
                              >
                                {app.status === 'accepted'
                                  ? 'Принят'
                                  : app.status === 'rejected'
                                  ? 'Отклонен'
                                  : app.status === 'reserved'
                                  ? 'В резерве'
                                  : 'На рассмотрении'}
                              </Badge>
                            </div>

                            {app.message && (
                              <p className="text-sm text-gray-600 mb-3 bg-gray-50 p-2 rounded">{app.message}</p>
                            )}

                            <div className="flex gap-2">
                              <Button size="sm" onClick={() => handleUpdateApplicationStatus(app.id, 'accepted')}>
                                <CheckCircle className="w-4 h-4 mr-1" />
                                Принять
                              </Button>
                              <Button
                                size="sm"
                                variant="outline"
                                onClick={() => handleUpdateApplicationStatus(app.id, 'reserved')}
                              >
                                <Clock className="w-4 h-4 mr-1" />
                                В резерв
                              </Button>
                              <Button
                                size="sm"
                                variant="destructive"
                                onClick={() => handleUpdateApplicationStatus(app.id, 'rejected')}
                              >
                                <XCircle className="w-4 h-4 mr-1" />
                                Отклонить
                              </Button>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  )}
                </CardContent>
              </Card>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
