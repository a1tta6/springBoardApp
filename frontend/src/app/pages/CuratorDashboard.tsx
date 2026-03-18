import React, { useState } from 'react';
import { useNavigate } from 'react-router';
import { useAuth } from '../context/AuthContext';
import { Button } from '../components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { Input } from '../components/ui/input';
import { Badge } from '../components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../components/ui/tabs';
import { Avatar, AvatarFallback } from '../components/ui/avatar';
import { toast } from 'sonner';
import {
  LogOut,
  Home,
  Shield,
  Building2,
  Users,
  Briefcase,
  CheckCircle,
  XCircle,
  Search,
  UserPlus,
} from 'lucide-react';
import { companies, users, opportunities } from '../data/mockData';

export const CuratorDashboard: React.FC = () => {
  const navigate = useNavigate();
  const { currentUser, logout } = useAuth();
  const [searchQuery, setSearchQuery] = useState('');

  const handleLogout = () => {
    logout();
    toast.success('Вы вышли из системы');
    navigate('/');
  };

  const handleVerifyCompany = (companyId: string) => {
    const company = companies.find((c) => c.id === companyId);
    if (company) {
      company.verified = true;
      toast.success(`Компания "${company.name}" верифицирована`);
    }
  };

  const handleRejectCompany = (companyId: string) => {
    const company = companies.find((c) => c.id === companyId);
    if (company) {
      toast.error(`Верификация компании "${company.name}" отклонена`);
    }
  };

  const handleApproveOpportunity = (oppId: string) => {
    const opp = opportunities.find((o) => o.id === oppId);
    if (opp) {
      opp.status = 'active';
      toast.success(`Публикация "${opp.title}" одобрена`);
    }
  };

  const handleRejectOpportunity = (oppId: string) => {
    const opp = opportunities.find((o) => o.id === oppId);
    if (opp) {
      opp.status = 'closed';
      toast.error(`Публикация "${opp.title}" отклонена`);
    }
  };

  const handleBlockUser = (userId: string) => {
    const user = users.find((u) => u.id === userId);
    if (user) {
      toast.success(`Пользователь ${user.displayName} заблокирован`);
    }
  };

  if (!currentUser || currentUser.role !== 'curator') {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Card>
          <CardHeader>
            <CardTitle>Доступ запрещен</CardTitle>
            <CardDescription>Эта страница доступна только для кураторов</CardDescription>
          </CardHeader>
          <CardContent>
            <Button onClick={() => navigate('/')}>На главную</Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  const pendingCompanies = companies.filter((c) => !c.verified);
  const verifiedCompanies = companies.filter((c) => c.verified);
  const applicants = users.filter((u) => u.role === 'applicant');
  const employers = users.filter((u) => u.role === 'employer');
  const curators = users.filter((u) => u.role === 'curator');

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white border-b">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-gradient-to-br from-purple-500 to-pink-600 rounded-lg flex items-center justify-center">
                <Shield className="w-6 h-6 text-white" />
              </div>
              <h1 className="text-xl font-bold">Панель администратора</h1>
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
        <div className="grid grid-cols-1 lg:grid-cols-4 gap-6 mb-6">
          <Card>
            <CardHeader className="pb-3">
              <CardTitle className="text-sm">Всего компаний</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold">{companies.length}</div>
              <p className="text-sm text-gray-500">
                Верифицировано: {verifiedCompanies.length}
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="pb-3">
              <CardTitle className="text-sm">Соискатели</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold">{applicants.length}</div>
              <p className="text-sm text-gray-500">Активных пользователей</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="pb-3">
              <CardTitle className="text-sm">Вакансии</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold">{opportunities.length}</div>
              <p className="text-sm text-gray-500">
                Активных: {opportunities.filter((o) => o.status === 'active').length}
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="pb-3">
              <CardTitle className="text-sm">Кураторы</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold">{curators.length}</div>
              <p className="text-sm text-gray-500">Администраторов платформы</p>
            </CardContent>
          </Card>
        </div>

        <Tabs defaultValue="verification" className="space-y-4">
          <TabsList>
            <TabsTrigger value="verification">
              <Building2 className="w-4 h-4 mr-2" />
              Верификация компаний
            </TabsTrigger>
            <TabsTrigger value="companies">
              <Building2 className="w-4 h-4 mr-2" />
              Все компании
            </TabsTrigger>
            <TabsTrigger value="users">
              <Users className="w-4 h-4 mr-2" />
              Пользователи
            </TabsTrigger>
            <TabsTrigger value="opportunities">
              <Briefcase className="w-4 h-4 mr-2" />
              Модерация публикаций
            </TabsTrigger>
            <TabsTrigger value="curators">
              <Shield className="w-4 h-4 mr-2" />
              Кураторы
            </TabsTrigger>
          </TabsList>

          <TabsContent value="verification" className="space-y-4">
            <Card>
              <CardHeader>
                <CardTitle>Ожидают верификации</CardTitle>
                <CardDescription>Проверка компаний перед допуском к платформе</CardDescription>
              </CardHeader>
              <CardContent>
                {pendingCompanies.length === 0 ? (
                  <div className="text-center py-8 text-gray-500">
                    <CheckCircle className="w-12 h-12 mx-auto mb-3 text-gray-300" />
                    <p>Нет компаний, ожидающих верификации</p>
                  </div>
                ) : (
                  <div className="space-y-4">
                    {pendingCompanies.map((company) => (
                      <div key={company.id} className="border rounded-lg p-4">
                        <div className="flex justify-between items-start mb-3">
                          <div className="flex-1">
                            <h4 className="font-semibold text-lg">{company.name}</h4>
                            <p className="text-sm text-gray-500">{company.industry}</p>
                            <p className="text-sm text-gray-600 mt-2">{company.description}</p>
                          </div>
                          <Badge variant="secondary">Не верифицирована</Badge>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-2 mb-3 text-sm">
                          <div>
                            <span className="text-gray-500">Email:</span> {company.email}
                          </div>
                          {company.website && (
                            <div>
                              <span className="text-gray-500">Сайт:</span>{' '}
                              <a href={company.website} target="_blank" rel="noopener noreferrer" className="text-blue-600 hover:underline">
                                {company.website}
                              </a>
                            </div>
                          )}
                        </div>

                        <div className="bg-blue-50 border border-blue-200 rounded p-3 mb-3">
                          <p className="text-sm text-blue-800">
                            <strong>Метод верификации:</strong> Проверка корпоративной почты, ИНН компании, наличие на
                            официальных реестрах. Рекомендуется запросить документы подтверждающие статус компании.
                          </p>
                        </div>

                        <div className="flex gap-2">
                          <Button onClick={() => handleVerifyCompany(company.id)}>
                            <CheckCircle className="w-4 h-4 mr-2" />
                            Верифицировать
                          </Button>
                          <Button variant="destructive" onClick={() => handleRejectCompany(company.id)}>
                            <XCircle className="w-4 h-4 mr-2" />
                            Отклонить
                          </Button>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="companies" className="space-y-4">
            <Card>
              <CardHeader>
                <CardTitle>Все компании</CardTitle>
                <CardDescription>Управление компаниями на платформе</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="mb-4">
                  <div className="relative">
                    <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                    <Input
                      placeholder="Поиск по названию компании..."
                      value={searchQuery}
                      onChange={(e) => setSearchQuery(e.target.value)}
                      className="pl-10"
                    />
                  </div>
                </div>

                <div className="space-y-3">
                  {companies
                    .filter((c) => c.name.toLowerCase().includes(searchQuery.toLowerCase()))
                    .map((company) => (
                      <div key={company.id} className="border rounded-lg p-4">
                        <div className="flex justify-between items-start">
                          <div className="flex-1">
                            <h4 className="font-semibold">{company.name}</h4>
                            <p className="text-sm text-gray-500">{company.industry}</p>
                            <p className="text-sm text-gray-600 mt-1">{company.email}</p>
                          </div>
                          <Badge variant={company.verified ? 'default' : 'secondary'}>
                            {company.verified ? 'Верифицирована' : 'Не верифицирована'}
                          </Badge>
                        </div>
                      </div>
                    ))}
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="users" className="space-y-4">
            <Card>
              <CardHeader>
                <CardTitle>Пользователи платформы</CardTitle>
                <CardDescription>Управление учетными записями</CardDescription>
              </CardHeader>
              <CardContent>
                <Tabs defaultValue="applicants">
                  <TabsList className="mb-4">
                    <TabsTrigger value="applicants">Соискатели ({applicants.length})</TabsTrigger>
                    <TabsTrigger value="employers">Работодатели ({employers.length})</TabsTrigger>
                  </TabsList>

                  <TabsContent value="applicants">
                    <div className="space-y-3">
                      {applicants.map((user) => (
                        <div key={user.id} className="border rounded-lg p-3 flex justify-between items-center">
                          <div>
                            <p className="font-medium">{user.displayName}</p>
                            <p className="text-sm text-gray-500">{user.email}</p>
                            {user.university && <p className="text-sm text-gray-600">{user.university}</p>}
                          </div>
                          <div className="flex gap-2">
                            <Button size="sm" variant="outline">
                              Просмотр
                            </Button>
                            <Button size="sm" variant="destructive" onClick={() => handleBlockUser(user.id)}>
                              Блокировать
                            </Button>
                          </div>
                        </div>
                      ))}
                    </div>
                  </TabsContent>

                  <TabsContent value="employers">
                    <div className="space-y-3">
                      {employers.map((user) => {
                        const company = companies.find((c) => c.id === user.companyId);
                        return (
                          <div key={user.id} className="border rounded-lg p-3 flex justify-between items-center">
                            <div>
                              <p className="font-medium">{user.displayName}</p>
                              <p className="text-sm text-gray-500">{user.email}</p>
                              {company && <p className="text-sm text-gray-600">{company.name}</p>}
                            </div>
                            <div className="flex gap-2">
                              <Button size="sm" variant="outline">
                                Просмотр
                              </Button>
                              <Button size="sm" variant="destructive" onClick={() => handleBlockUser(user.id)}>
                                Блокировать
                              </Button>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  </TabsContent>
                </Tabs>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="opportunities" className="space-y-4">
            <Card>
              <CardHeader>
                <CardTitle>Модерация публикаций</CardTitle>
                <CardDescription>Проверка вакансий и мероприятий</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  {opportunities.map((opp) => {
                    const company = companies.find((c) => c.id === opp.companyId);
                    return (
                      <div key={opp.id} className="border rounded-lg p-4">
                        <div className="flex justify-between items-start mb-2">
                          <div className="flex-1">
                            <h4 className="font-semibold">{opp.title}</h4>
                            <p className="text-sm text-gray-500">{company?.name}</p>
                            <p className="text-sm text-gray-600 mt-1 line-clamp-2">{opp.description}</p>
                          </div>
                          <Badge variant={opp.status === 'active' ? 'default' : 'secondary'}>{opp.status}</Badge>
                        </div>

                        <div className="flex gap-2 mt-3">
                          <Badge variant="outline">{opp.type}</Badge>
                          <Badge variant="outline">{opp.workFormat}</Badge>
                          <Badge variant="outline">{opp.location.city}</Badge>
                        </div>

                        <div className="flex gap-2 mt-3">
                          <Button size="sm" onClick={() => handleApproveOpportunity(opp.id)}>
                            <CheckCircle className="w-4 h-4 mr-1" />
                            Одобрить
                          </Button>
                          <Button size="sm" variant="destructive" onClick={() => handleRejectOpportunity(opp.id)}>
                            <XCircle className="w-4 h-4 mr-1" />
                            Отклонить
                          </Button>
                        </div>
                      </div>
                    );
                  })}
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="curators" className="space-y-4">
            <Card>
              <CardHeader>
                <div className="flex justify-between items-center">
                  <div>
                    <CardTitle>Кураторы платформы</CardTitle>
                    <CardDescription>Управление администраторами</CardDescription>
                  </div>
                  <Button>
                    <UserPlus className="w-4 h-4 mr-2" />
                    Добавить куратора
                  </Button>
                </div>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  {curators.map((curator) => (
                    <div key={curator.id} className="border rounded-lg p-3 flex justify-between items-center">
                      <div className="flex items-center gap-3">
                        <Avatar>
                          <AvatarFallback className="bg-purple-600 text-white">
                            {curator.displayName.charAt(0)}
                          </AvatarFallback>
                        </Avatar>
                        <div>
                          <p className="font-medium">{curator.displayName}</p>
                          <p className="text-sm text-gray-500">{curator.email}</p>
                        </div>
                      </div>
                      {curator.id !== 'u3' && (
                        <Button size="sm" variant="outline">
                          Удалить
                        </Button>
                      )}
                      {curator.id === 'u3' && (
                        <Badge variant="default">Главный администратор</Badge>
                      )}
                    </div>
                  ))}
                </div>

                <div className="mt-4 bg-blue-50 border border-blue-200 rounded-lg p-4">
                  <p className="text-sm text-blue-800">
                    <strong>Примечание:</strong> Только главный администратор может создавать новых кураторов платформы.
                    Кураторы имеют полный доступ к управлению контентом и пользователями.
                  </p>
                </div>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
};
