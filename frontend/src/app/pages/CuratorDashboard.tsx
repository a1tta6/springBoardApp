import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { useAuth } from '../context/AuthContext';
import { Company, Opportunity, User } from '../types';
import { Button } from '../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { Input } from '../components/ui/input';
import { Badge } from '../components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../components/ui/tabs';
import { toast } from 'sonner';
import { LogOut, Home, Shield, CheckCircle, XCircle, Search } from 'lucide-react';
import { appApi } from '../api/appApi';

export const CuratorDashboard: React.FC = () => {
  const navigate = useNavigate();
  const { currentUser, logout } = useAuth();
  const [searchQuery, setSearchQuery] = useState('');
  const [companies, setCompanies] = useState<Company[]>([]);
  const [pendingCompanies, setPendingCompanies] = useState<Company[]>([]);
  const [pendingOpportunities, setPendingOpportunities] = useState<Opportunity[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function load() {
      try {
        const [nextCompanies, nextPendingCompanies, nextPendingOpportunities, nextUsers] = await Promise.all([
          appApi.getCompanies(),
          appApi.getCuratorPendingCompanies(),
          appApi.getCuratorPendingOpportunities(),
          appApi.getCuratorUsers(),
        ]);
        setCompanies(nextCompanies);
        setPendingCompanies(nextPendingCompanies);
        setPendingOpportunities(nextPendingOpportunities);
        setUsers(nextUsers);
      } catch (error) {
        toast.error(error instanceof Error ? error.message : 'Ошибка при загрузке профиля куратора');
      } finally {
        setIsLoading(false);
      }
    }

    if (currentUser?.role === 'curator') {
      void load();
    }
  }, [currentUser]);

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  const handleVerifyCompany = async (companyId: string) => {
    try {
      await appApi.verifyCompany(companyId);
      setPendingCompanies((prev) => prev.filter((item) => item.id !== companyId));
      setCompanies((prev) => prev.map((item) => (item.id === companyId ? { ...item, verified: true } : item)));
      toast.success('Компания верифицирована');
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Ошибка при верификации компании');
    }
  };

  const handleModerateOpportunity = async (opportunityId: string, status: 'active' | 'closed') => {
    try {
      await appApi.moderateOpportunity(opportunityId, status);
      setPendingOpportunities((prev) => prev.filter((item) => item.id !== opportunityId));
      toast.success('Возможность обработана');
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Ошибка при модерации возможности');
    }
  };

  const handleBlockUser = async (userId: string) => {
    try {
      await appApi.blockUser(userId);
      setUsers((prev) => prev.filter((item) => item.id !== userId));
      toast.success('Пользователь заблокирован');
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Ошибка при блокировки пользователя');
    }
  };

  if (!currentUser || currentUser.role !== 'curator') {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Card>
          <CardContent className="pt-6">
            <Button onClick={() => navigate('/')}>На главную</Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  const applicants = users.filter((item) => item.role === 'applicant');
  const employers = users.filter((item) => item.role === 'employer');
  const curators = users.filter((item) => item.role === 'curator');

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white border-b">
        <div className="container mx-auto px-4 py-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-gradient-to-br from-purple-500 to-pink-600 rounded-lg flex items-center justify-center" onClick={() => navigate('/')}>
              <Shield className="w-6 h-6 text-white" />
            </div>
            <h1 className="text-xl font-bold">Профиль куратора</h1>
          </div>
          <div className="flex gap-3">
            <Button variant="outline" onClick={() => navigate('/')}><Home className="w-4 h-4 mr-2" />На главную</Button>
            <Button variant="outline" onClick={() => void handleLogout()}><LogOut className="w-4 h-4 mr-2" />Выход</Button>
          </div>
        </div>
      </header>

      <div className="container mx-auto px-4 py-6">
        <Tabs defaultValue="verification">
          <TabsList>
            <TabsTrigger value="verification">Верификации</TabsTrigger>
            <TabsTrigger value="companies">Компании</TabsTrigger>
            <TabsTrigger value="users">Пользователи</TabsTrigger>
            <TabsTrigger value="opportunities">Возможности</TabsTrigger>
            <TabsTrigger value="curators">Кураторы</TabsTrigger>
          </TabsList>

          <TabsContent value="verification">
            <Card>
              <CardHeader><CardTitle>Компании в обработке</CardTitle></CardHeader>
              <CardContent className="space-y-3">
                {isLoading ? <p className="text-gray-500">Загрузка...</p> : pendingCompanies.map((company) => (
                  <div key={company.id} className="border rounded-lg p-4">
                    <h4 className="font-semibold">{company.name}</h4>
                    <p className="text-sm text-gray-500">{company.email}</p>
                    <Button className="mt-3" onClick={() => void handleVerifyCompany(company.id)}><CheckCircle className="w-4 h-4 mr-2" />Верифицировать</Button>
                  </div>
                ))}
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="companies">
            <Card>
              <CardHeader><CardTitle>Компании</CardTitle></CardHeader>
              <CardContent>
                <div className="relative mb-4">
                  <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 w-4 h-4" />
                  <Input className="pl-10" value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)} />
                </div>
                <div className="space-y-3">
                  {companies.filter((company) => company.name.toLowerCase().includes(searchQuery.toLowerCase())).map((company) => (
                    <div key={company.id} className="border rounded-lg p-4 flex justify-between">
                      <div>
                        <h4 className="font-semibold">{company.name}</h4>
                        <p className="text-sm text-gray-500">{company.email}</p>
                      </div>
                      <Badge>{company.verified ? 'Компания верифицирована' : 'В обработке'}</Badge>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="users">
            <Card>
              <CardHeader><CardTitle>Пользователи</CardTitle></CardHeader>
              <CardContent className="space-y-3">
                {[...applicants, ...employers].map((user) => (
                  <div key={user.id} className="border rounded-lg p-4 flex justify-between">
                    <div>
                      <h4 className="font-semibold">{user.displayName}</h4>
                      <p className="text-sm text-gray-500">{user.email}</p>
                    </div>
                    <Button variant="destructive" onClick={() => void handleBlockUser(user.id)}><XCircle className="w-4 h-4 mr-2" />Заблокировать</Button>
                  </div>
                ))}
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="opportunities">
            <Card>
              <CardHeader><CardTitle>Возможности в обработке</CardTitle></CardHeader>
              <CardContent className="space-y-3">
                {pendingOpportunities.map((opportunity) => (
                  <div key={opportunity.id} className="border rounded-lg p-4">
                    <h4 className="font-semibold">{opportunity.title}</h4>
                    <p className="text-sm text-gray-600 mt-2">{opportunity.description}</p>
                    <div className="flex gap-2 mt-3">
                      <Button onClick={() => void handleModerateOpportunity(opportunity.id, 'active')}><CheckCircle className="w-4 h-4 mr-2" />Одобрить</Button>
                      <Button variant="destructive" onClick={() => void handleModerateOpportunity(opportunity.id, 'closed')}><XCircle className="w-4 h-4 mr-2" />Отклонить</Button>
                    </div>
                  </div>
                ))}
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="curators">
            <Card>
              <CardHeader><CardTitle>Кураторы</CardTitle></CardHeader>
              <CardContent className="space-y-3">
                {curators.map((curator) => (
                  <div key={curator.id} className="border rounded-lg p-4">
                    <h4 className="font-semibold">{curator.displayName}</h4>
                    <p className="text-sm text-gray-500">{curator.email}</p>
                  </div>
                ))}
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
};
