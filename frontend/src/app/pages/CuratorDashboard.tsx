import React, { useEffect, useState } from 'react';
import { useNavigate, Link } from 'react-router';
import { useAuth } from '../context/AuthContext';
import { Company, Opportunity, User } from '../types';
import { Button } from '../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '../components/ui/card';
import { Input } from '../components/ui/input';
import { Badge } from '../components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../components/ui/tabs';
import { Textarea } from '../components/ui/textarea';
import { Avatar, AvatarFallback } from '../components/ui/avatar';
import { toast } from 'sonner';
import { LogOut, Home, Shield, CheckCircle, XCircle, Search, AlertCircle, Clock } from 'lucide-react';
import { appApi, VerificationRequest } from '../api/appApi';

export const CuratorDashboard: React.FC = () => {
  const navigate = useNavigate();
  const { currentUser, logout } = useAuth();
  const [searchQuery, setSearchQuery] = useState('');
  const [companies, setCompanies] = useState<Company[]>([]);
  const [pendingCompanies, setPendingCompanies] = useState<Company[]>([]);
  const [verificationRequests, setVerificationRequests] = useState<VerificationRequest[]>([]);
  const [pendingOpportunities, setPendingOpportunities] = useState<Opportunity[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [rejectReason, setRejectReason] = useState<Record<string, string>>({});
  const [showRejectForm, setShowRejectForm] = useState<Record<string, boolean>>({});

  useEffect(() => {
    async function load() {
      try {
        const [nextCompanies, nextPendingCompanies, nextPendingVerifications, nextPendingOpportunities, nextUsers] = await Promise.all([
          appApi.getCompanies(),
          appApi.getCuratorPendingCompanies(),
          appApi.getCuratorPendingVerifications(),
          appApi.getCuratorPendingOpportunities(),
          appApi.getCuratorUsers(),
        ]);
        setCompanies(nextCompanies);
        setPendingCompanies(nextPendingCompanies);
        setVerificationRequests(nextPendingVerifications);
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

  const handleApproveVerification = async (requestId: string) => {
    try {
      await appApi.approveVerification(requestId);
      setVerificationRequests((prev) => prev.filter((item) => item.id !== requestId));
      const company = verificationRequests.find(r => r.id === requestId);
      if (company) {
        setCompanies((prev) => prev.map((item) => (item.id === company.companyId ? { ...item, verified: true } : item)));
      }
      toast.success('Верификация одобрена');
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Ошибка при одобрении верификации');
    }
  };

  const handleRejectVerification = async (requestId: string) => {
    const reason = rejectReason[requestId];
    if (!reason || reason.trim() === '') {
      toast.error('Укажите причину отклонения');
      return;
    }
    try {
      await appApi.rejectVerification(requestId, reason);
      setVerificationRequests((prev) => prev.filter((item) => item.id !== requestId));
      setShowRejectForm((prev) => ({ ...prev, [requestId]: false }));
      setRejectReason((prev) => ({ ...prev, [requestId]: '' }));
      toast.success('Верификация отклонена');
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Ошибка при отклонении верификации');
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
              <CardHeader>
                <CardTitle>Заявки на верификацию</CardTitle>
                <CardDescription>Рассмотрение заявок работодателей на верификацию компании</CardDescription>
              </CardHeader>
              <CardContent className="space-y-3">
                {isLoading ? <p className="text-gray-500">Загрузка...</p> : verificationRequests.length === 0 ? (
                  <p className="text-gray-500">Нет заявок на верификацию</p>
                ) : verificationRequests.map((request) => {
                  const company = companies.find(c => c.id === request.companyId);
                  return (
                    <div key={request.id} className="border rounded-lg p-4">
                      <div className="flex justify-between items-start">
                        <div className="flex-1">
                          <Link to={`/company/${company?.id}`} className="font-semibold hover:text-blue-600">
                            {company?.name || 'Компания'}
                          </Link>
                          <p className="text-sm text-gray-500">{company?.email}</p>
                          {company?.inn && <p className="text-sm text-gray-500">ИНН: {company.inn}</p>}
                          {company?.ogrn && <p className="text-sm text-gray-500">ОГРН: {company.ogrn}</p>}
                          {company?.address && <p className="text-sm text-gray-600 mt-1">{company.address}</p>}
                          {company?.socialLinks && <p className="text-sm text-gray-600 mt-2">{company.socialLinks}</p>}
                        </div>
                        <Badge variant="secondary">
                          <Clock className="w-3 h-3 mr-1" />
                          Ожидает
                        </Badge>
                      </div>
                      
                      {showRejectForm[request.id] ? (
                        <div className="mt-4 space-y-2">
                          <Textarea 
                            placeholder="Причина отклонения..."
                            value={rejectReason[request.id] || ''}
                            onChange={(e) => setRejectReason(prev => ({ ...prev, [request.id]: e.target.value }))}
                          />
                          <div className="flex gap-2">
                            <Button size="sm" variant="destructive" onClick={() => handleRejectVerification(request.id)}>
                              Отклонить
                            </Button>
                            <Button size="sm" variant="outline" onClick={() => setShowRejectForm(prev => ({ ...prev, [request.id]: false }))}>
                              Отмена
                            </Button>
                          </div>
                        </div>
                      ) : (
                        <div className="flex gap-2 mt-3">
                          <Button size="sm" onClick={() => handleApproveVerification(request.id)}>
                            <CheckCircle className="w-4 h-4 mr-1" />
                            Одобрить
                          </Button>
                          <Button size="sm" variant="outline" onClick={() => setShowRejectForm(prev => ({ ...prev, [request.id]: true }))}>
                            <XCircle className="w-4 h-4 mr-1" />
                            Отклонить
                          </Button>
                        </div>
                      )}
                    </div>
                  );
                })}
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
                    <Link key={company.id} to={`/company/${company.id}`} className="border rounded-lg p-4 flex justify-between hover:bg-gray-50">
                      <div>
                        <h4 className="font-semibold hover:text-blue-600">{company.name}</h4>
                        <p className="text-sm text-gray-500">{company.email}</p>
                      </div>
                      <Badge>{company.verified ? 'Компания верифицирована' : 'В обработке'}</Badge>
                    </Link>
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
                    <div className="flex items-center gap-4">
                      <Avatar className="w-10 h-10 border bg-gray-50">
                        {user.photo ? (
                          <img src={user.photo} alt={user.displayName} className="w-full h-full object-cover" />
                        ) : (
                          <AvatarFallback className="bg-gradient-to-br from-purple-500 to-pink-600 text-white text-xs">
                            {user.displayName.charAt(0)}
                          </AvatarFallback>
                        )}
                      </Avatar>
                      <div>
                        <h4 className="font-semibold">{user.displayName}</h4>
                        <p className="text-sm text-gray-500">{user.email}</p>
                      </div>
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
                {pendingOpportunities.length === 0 ? (
                  <p className="text-gray-500">Нет возможностей на модерации</p>
                ) : (
                  pendingOpportunities.map((opportunity) => {
                    const company = companies.find(c => c.id === opportunity.companyId);
                    return (
                      <div key={opportunity.id} className="border rounded-lg p-4">
                        <div className="flex justify-between items-start">
                          <div className="flex-1">
                            <Link to={`/opportunity/${opportunity.id}`} className="font-semibold hover:text-blue-600">
                              {opportunity.title}
                            </Link>
                            <p className="text-sm text-gray-500 mt-1">
                              {company?.name || 'Компания'}
                            </p>
                            <p className="text-sm text-gray-600 mt-2 line-clamp-2">{opportunity.description}</p>
                          </div>
                        </div>
                        <div className="flex gap-2 mt-3">
                          <Button onClick={() => void handleModerateOpportunity(opportunity.id, 'active')}><CheckCircle className="w-4 h-4 mr-2" />Одобрить</Button>
                          <Button variant="destructive" onClick={() => void handleModerateOpportunity(opportunity.id, 'closed')}><XCircle className="w-4 h-4 mr-2" />Отклонить</Button>
                          <Link to={`/opportunity/${opportunity.id}`}>
                            <Button variant="outline">Подробнее</Button>
                          </Link>
                        </div>
                      </div>
                    );
                  })
                )}
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
