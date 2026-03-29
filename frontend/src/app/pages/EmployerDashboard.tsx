import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { useAuth } from '../context/AuthContext';
import { Application, Company, Opportunity, User, statusMap, Recommendation } from '../types';
import { Button } from '../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '../components/ui/card';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Textarea } from '../components/ui/textarea';
import { Badge } from '../components/ui/badge';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../components/ui/select';
import { toast } from 'sonner';
import { LogOut, Home, Building2, Briefcase, Users, CheckCircle, Clock, XCircle, Search, Plus, Edit, Trash2, ExternalLink, Globe, Send, AlertCircle, Star } from 'lucide-react';
import { appApi, VerificationRequest } from '../api/appApi';

type OpportunityStatus = 'active' | 'closed' | 'planned';
type OpportunityType = 'vacancy' | 'internship' | 'mentorship' | 'event';
type WorkFormat = 'office' | 'hybrid' | 'remote';

export const EmployerDashboard: React.FC = () => {
  const navigate = useNavigate();
  const { currentUser, logout } = useAuth();
  const [activeTab, setActiveTab] = useState<'company' | 'opportunities' | 'applications' | 'recommendations'>('company');
  const [companies, setCompanies] = useState<Company[]>([]);
  const [opportunities, setOpportunities] = useState<Opportunity[]>([]);
  const [applications, setApplications] = useState<Application[]>([]);
  const [recommendations, setRecommendations] = useState<Recommendation[]>([]);
  const [applicants, setApplicants] = useState<User[]>([]);
  const [innError, setInnError] = useState<string>("");
  const [ogrnError, setOgrnError] = useState<string>("");
  const [verificationRequest, setVerificationRequest] = useState<VerificationRequest | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isCreating, setIsCreating] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [isEditingCompany, setIsEditingCompany] = useState(false);
  
  // Фильтры
  const [oppStatusFilter, setOppStatusFilter] = useState<string>('all');
  const [appSearchQuery, setAppSearchQuery] = useState('');

  const [companyForm, setCompanyForm] = useState({
    name: '',
    inn: '',
    ogrn: '',
    address: '',
    website: '',
    logo: '',
    socialLinks: '',
    bio: '',
    email: '',
  });

  const [oppForm, setOppForm] = useState({
    title: '',
    description: '',
    type: 'vacancy' as OpportunityType,
    workFormat: 'office' as WorkFormat,
    city: '',
    address: '',
    latitude: 55.751244,
    longitude: 37.618423,
    salaryMin: 0,
    salaryMax: 0,
    currency: 'RUB',
    contactEmail: currentUser?.email || '',
    contactPhone: '',
    contactWebsite: '',
    requirements: '',
    eventDate: '',
    expiryDate: '',
  });

  const company = companies.find((item) => item.id === currentUser?.companyId);
  const isVerified = company?.verified || false;
  const isPending = verificationRequest?.status === 'pending';
  const isRejected = verificationRequest?.status === 'rejected';
  const canEditCompany = !isPending;

  useEffect(() => {
    async function load() {
      try {
        const [nextCompanies, nextOpportunities, nextApplications, nextApplicants, verificationData, nextRecs] = await Promise.all([
          appApi.getCompanies(),
          appApi.getEmployerOpportunities(),
          appApi.getEmployerApplications(),
          appApi.getEmployerApplicants(),
          appApi.getVerificationStatus(),
          appApi.getEmployerRecommendations(),
        ]);
        setCompanies(nextCompanies);
        setOpportunities(nextOpportunities);
        setApplications(nextApplications);
        setApplicants(nextApplicants);
        setVerificationRequest(verificationData);
        setRecommendations(nextRecs);
        
        if (nextCompanies.length > 0) {
          const myCompany = nextCompanies.find((c) => c.id === currentUser?.companyId);
          if (myCompany) {
            let parsedSocialLinks = '';
            if (myCompany.socialLinks) {
              try {
                parsedSocialLinks = myCompany.socialLinks;
              } catch (e) {
                parsedSocialLinks = '';
              }
            }
            setCompanyForm({
              name: myCompany.name || '',
              inn: myCompany.inn || '',
              ogrn: myCompany.ogrn || '',
              address: myCompany.address || '',
              website: myCompany.website || '',
              logo: myCompany.logo || '',
              socialLinks: parsedSocialLinks,
              bio: myCompany.bio || '',
              email: myCompany.email || '',
            });
          }
        }
      } catch (error) {
        toast.error(error instanceof Error ? error.message : 'Ошибка при загрузке профиля работодателя');
      } finally {
        setIsLoading(false);
      }
    }

    if (currentUser?.role === 'employer') {
      void load();
    }
  }, [currentUser]);

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  const handleCompanySave = async () => {
    try {
      await appApi.updateCompanyProfile(companyForm);
      const updatedCompanies = await appApi.getCompanies();
      setCompanies(updatedCompanies);
      setIsEditingCompany(false);
      toast.success('Изменения сохранены');
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Ошибка при сохранении');
    }
  };

  const handleSubmitVerification = async () => {
    try {
      await appApi.submitVerification();
      const verificationData = await appApi.getVerificationStatus();
      setVerificationRequest(verificationData);
      toast.success('Заявка на верификацию отправлена');
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Ошибка при отправке заявки');
    }
  };

  const resetOppForm = () => {
    setOppForm({
      title: '',
      description: '',
      type: 'vacancy',
      workFormat: 'office',
      city: '',
      address: '',
      latitude: 55.751244,
      longitude: 37.618423,
      salaryMin: 0,
      salaryMax: 0,
      currency: 'RUB',
      contactEmail: currentUser?.email || '',
      contactPhone: '',
      contactWebsite: '',
      requirements: '',
      eventDate: '',
      expiryDate: '',
    });
    setIsCreating(false);
    setEditingId(null);
  };

  const handleCreateOpp = async () => {
    try {
      const created = await appApi.createEmployerOpportunity(oppForm);
      setOpportunities((prev) => [created, ...prev]);
      toast.success('Возможность создана');
      resetOppForm();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Ошибка при создании возможности');
    }
  };

  const handleUpdateOpp = async () => {
    if (!editingId) return;
    try {
      const updatedOpp = await appApi.updateEmployerOpportunity(editingId, oppForm);
      setOpportunities((prev) => prev.map((opp) => (opp.id === editingId ? updatedOpp : opp)));
      toast.success('Возможность обновлена');
      resetOppForm();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Ошибка при обновлении возможности');
    }
  };

  const handleDeleteOpp = async (id: string) => {
    try {
      await appApi.deleteOpportunity(id);
      setOpportunities((prev) => prev.filter((opp) => opp.id !== id));
      toast.success('Возможность удалена');
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Ошибка при удалении возможности');
    }
  };

  const handleEditOpp = (opp: Opportunity) => {
    setOppForm({
      title: opp.title,
      description: opp.description,
      type: opp.type as OpportunityType,
      workFormat: opp.workFormat as WorkFormat,
      city: opp.location.city,
      address: opp.location.address || '',
      latitude: opp.location.coordinates[0],
      longitude: opp.location.coordinates[1],
      salaryMin: opp.salary?.min || 0,
      salaryMax: opp.salary?.max || 0,
      currency: opp.salary?.currency || 'RUB',
      contactEmail: opp.contactInfo.email || '',
      contactPhone: opp.contactInfo.phone || '',
      contactWebsite: opp.contactInfo.website || '',
      requirements: opp.requirements || '',
      eventDate: opp.eventDate ? new Date(opp.eventDate).toISOString().split('T')[0] : '',
      expiryDate: opp.expiryDate ? new Date(opp.expiryDate).toISOString().split('T')[0] : '',
    });
    setEditingId(opp.id);
    setIsCreating(true);
  };

  const handleStatus = async (applicationId: string, status: string) => {
    try {
      await appApi.updateEmployerApplicationStatus(applicationId, status);
      setApplications((prev) => prev.map((item) => (item.id === applicationId ? { ...item, status: status as Application['status'] } : item)));
      toast.success('Отклик обновлен');
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Ошибка при обновлении отклика');
    }
  };

  const filteredOpportunities = opportunities.filter((opp) => {
    if (oppStatusFilter === 'all') return true;
    return opp.status === oppStatusFilter;
  });

  const filteredApplications = applications.filter((app) => {
    const applicant = applicants.find((a) => a.id === app.applicantId);
    if (!appSearchQuery) return true;
    const query = appSearchQuery.toLowerCase();
    return (
      applicant?.displayName?.toLowerCase().includes(query) ||
      applicant?.email?.toLowerCase().includes(query) ||
      applicant?.fullName?.toLowerCase().includes(query)
    );
  });

  if (!currentUser || currentUser.role !== 'employer') {
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

  const typeLabels: Record<OpportunityType, string> = {
    vacancy: 'Вакансия',
    internship: 'Стажировка',
    mentorship: 'Менторство',
    event: 'Мероприятие',
  };

  const formatLabels: Record<WorkFormat, string> = {
    office: 'Офис',
    hybrid: 'Гибрид',
    remote: 'Удаленно',
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white border-b">
        <div className="container mx-auto px-4 py-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-purple-600 rounded-lg flex items-center justify-center" onClick={() => navigate('/')}>
              <span className="text-white font-bold text-xl">T</span>
            </div>
            <h1 className="text-xl font-bold">Профиль работодателя</h1>
          </div>
          <div className="flex gap-3">
            <Button variant="outline" onClick={() => navigate('/')}><Home className="w-4 h-4 mr-2" />На главную</Button>
            <Button variant="outline" onClick={() => void handleLogout()}><LogOut className="w-4 h-4 mr-2" />Выход</Button>
          </div>
        </div>
      </header>

      {/* Bio editing moved into Company editing panel; top bio card removed per request */}

      <div className="container mx-auto px-4 py-6 grid grid-cols-1 lg:grid-cols-4 gap-6">
        <Card className="lg:col-span-1">
          <CardContent className="pt-6 space-y-2">
            <Button variant={activeTab === 'company' ? 'default' : 'ghost'} className="w-full justify-start" onClick={() => setActiveTab('company')}><Building2 className="w-4 h-4 mr-2" />Компания</Button>
            <Button variant={activeTab === 'opportunities' ? 'default' : 'ghost'} className="w-full justify-start" onClick={() => setActiveTab('opportunities')}><Briefcase className="w-4 h-4 mr-2" />Возможности</Button>
            <Button variant={activeTab === 'applications' ? 'default' : 'ghost'} className="w-full justify-start" onClick={() => setActiveTab('applications')}><Users className="w-4 h-4 mr-2" />Отклики</Button>
            <Button variant={activeTab === 'recommendations' ? 'default' : 'ghost'} className="w-full justify-start" onClick={() => setActiveTab('recommendations')}><Star className="w-4 h-4 mr-2" />Рекомендации</Button>
          </CardContent>
        </Card>

        <div className="lg:col-span-3">
          {activeTab === 'company' && (
            <Card>
              <CardHeader>
                <div className="flex justify-between items-center">
                  <div>
                    <CardTitle>Информация о компании</CardTitle>
                    <CardDescription>Заполните данные о вашей компании для верификации</CardDescription>
                  </div>
                  {canEditCompany && !isEditingCompany && (
                    <Button variant="outline" size="sm" onClick={() => setIsEditingCompany(true)}>
                      <Edit className="w-4 h-4 mr-2" />
                      Редактировать
                    </Button>
                  )}
                </div>
              </CardHeader>
              <CardContent className="space-y-4">
                {/* Статус верификации */}
                <div className="flex items-center gap-3 p-4 rounded-lg bg-gray-50">
                  {isVerified ? (
                    <>
                      <CheckCircle className="w-6 h-6 text-green-500" />
                      <div>
                        <p className="font-medium text-green-700">Компания верифицирована</p>
                        <p className="text-sm text-gray-500">Вы можете создавать вакансии и стажировки</p>
                      </div>
                    </>
                  ) : isPending ? (
                    <>
                      <Clock className="w-6 h-6 text-yellow-500" />
                      <div>
                        <p className="font-medium text-yellow-700">Заявка на верификацию рассматривается</p>
                        <p className="text-sm text-gray-500">Дождитесь проверки куратора</p>
                      </div>
                    </>
                  ) : isRejected ? (
                    <>
                      <AlertCircle className="w-6 h-6 text-red-500" />
                      <div className="flex-1">
                        <p className="font-medium text-red-700">Заявка отклонена</p>
                        <p className="text-sm text-gray-500">Причина: {verificationRequest?.rejectionReason}</p>
                      </div>
                    </>
                  ) : (
                    <>
                      <XCircle className="w-6 h-6 text-gray-400" />
                      <div>
                        <p className="font-medium text-gray-700">Компания не верифицирована</p>
                        <p className="text-sm text-gray-500">Заполните данные и отправьте на верификацию</p>
                      </div>
                    </>
                  )}
                </div>

                {(isEditingCompany || !isVerified) && (
                  <div className="space-y-4">
                    <div className="space-y-2">
                      <Label>Название компании *</Label>
                      <Input 
                        value={companyForm.name} 
                        onChange={(e) => setCompanyForm((prev) => ({ ...prev, name: e.target.value }))}
                        disabled={!canEditCompany}
                      />
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-2">
                        <Label>ИНН *</Label>
                        <Input 
                          value={companyForm.inn} 
                          onChange={(e) => { const v = e.target.value; setCompanyForm((prev) => ({ ...prev, inn: v })); if (innError) setInnError(''); }}
                          onBlur={() => { if (companyForm.inn && !/^\d{10}$/.test(companyForm.inn)) { setInnError('ИНН должен состоять из 10 цифр'); } else { setInnError(''); } }}
                          disabled={!canEditCompany}
                        />
                        {innError && <span className="text-xs text-red-600">{innError}</span>}
                      </div>
                      <div className="space-y-2">
                        <Label>ОГРН</Label>
                        <Input 
                          value={companyForm.ogrn} 
                          onChange={(e) => { const v = e.target.value; setCompanyForm((prev) => ({ ...prev, ogrn: v })); if (ogrnError) setOgrnError(''); }}
                          onBlur={() => { if (companyForm.ogrn && !/^\d{13}$/.test(companyForm.ogrn)) { setOgrnError('ОГРН должен состоять из 13 цифр'); } else { setOgrnError(''); } }}
                          disabled={!canEditCompany}
                        />
                        {ogrnError && <span className="text-xs text-red-600">{ogrnError}</span>}
                      </div>
                    </div>

                    <div className="space-y-2">
                      <Label>Адрес организации</Label>
                      <Input 
                        value={companyForm.address} 
                        onChange={(e) => setCompanyForm((prev) => ({ ...prev, address: e.target.value }))}
                        disabled={!canEditCompany}
                      />
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-2">
                        <Label>Website</Label>
                        <Input 
                          value={companyForm.website} 
                          onChange={(e) => setCompanyForm((prev) => ({ ...prev, website: e.target.value }))}
                          disabled={!canEditCompany}
                        />
                      </div>
                      <div className="space-y-2">
                        <Label>Email</Label>
                        <Input 
                          value={companyForm.email} 
                          disabled
                        />
                      </div>
                    </div>

                    <div className="space-y-2">
                      <Label>Логотип компании</Label>
                      <div className="flex items-center gap-4">
                        {companyForm.logo && (
                          <img src={companyForm.logo} alt="Logo" className="w-20 h-20 object-contain border rounded" />
                        )}
                        <Input 
                          type="file"
                          accept="image/*"
                          onChange={(e) => {
                            const file = e.target.files?.[0];
                            if (file) {
                              const reader = new FileReader();
                              reader.onloadend = () => {
                                setCompanyForm((prev) => ({ ...prev, logo: reader.result as string }));
                                toast.success('Логотип выбран');
                              };
                              reader.onerror = () => {
                                toast.error('Ошибка при чтении файла');
                              };
                              reader.readAsDataURL(file);
                            }
                          }}
                          disabled={!canEditCompany}
                        />
                      </div>
                    </div>

                    <div className="space-y-2">
                      <Label>Ссылки на соц. сети и контактные данные</Label>
                      <Textarea 
                        value={companyForm.socialLinks} 
                        onChange={(e) => setCompanyForm((prev) => ({ ...prev, socialLinks: e.target.value }))}
                        disabled={!canEditCompany}
                        rows={3}
                      />
                    </div>

                    <div className="space-y-2">
                      <Label>О компании</Label>
                      <Textarea value={companyForm.bio} onChange={(e) => setCompanyForm((prev) => ({ ...prev, bio: e.target.value }))} rows={3} />
                    </div>

                    {canEditCompany && (
                      <div className="flex gap-2 pt-2">
                        <Button onClick={handleCompanySave}>Сохранить</Button>
                        {isEditingCompany && (
                          <Button variant="outline" onClick={() => setIsEditingCompany(false)}>Отмена</Button>
                        )}
                        {!isVerified && !isPending && (
                          <Button onClick={handleSubmitVerification} variant="secondary">
                            <Send className="w-4 h-4 mr-2" />
                            Отправить на верификацию
                          </Button>
                        )}
                      </div>
                    )}
                  </div>
                )}

                
              </CardContent>
            </Card>
          )}

          {activeTab === 'opportunities' && (
            <Card>
              <CardHeader>
                <div className="flex justify-between items-center">
                  <CardTitle>Возможности</CardTitle>
                  {!isVerified && (
                    <p className="text-sm text-yellow-600">Для создания возможностей необходимо пройти верификацию</p>
                  )}
                  {isVerified && !isCreating && (
                    <Button size="sm" onClick={() => setIsCreating(true)}>
                      <Plus className="w-4 h-4 mr-2" />
                      Создать
                    </Button>
                  )}
                </div>
              </CardHeader>
              <CardContent className="space-y-4">
                {!isVerified && (
                  <div className="p-4 rounded-lg bg-yellow-50 border border-yellow-200">
                    <p className="text-yellow-800">Чтобы создавать вакансии и стажировки, необходимо пройти верификацию компании.</p>
                    <Button variant="outline" className="mt-2" onClick={() => setActiveTab('company')}>
                      Перейти к верификации
                    </Button>
                  </div>
                )}

                {isVerified && !isCreating && (
                  <div className="flex gap-2 mb-4">
                    <Select value={oppStatusFilter} onValueChange={setOppStatusFilter}>
                      <SelectTrigger className="w-[180px]">
                        <SelectValue placeholder="Статус" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="all">Все</SelectItem>
                        <SelectItem value="active">Активные</SelectItem>
                        <SelectItem value="closed">Закрытые</SelectItem>
                        <SelectItem value="planned">Запланированные</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                )}

                {isVerified && isCreating && (
                  <div className="border rounded-lg p-4 space-y-4 bg-gray-50">
                    <h4 className="font-semibold">{editingId ? 'Редактирование' : 'Создание новой возможности'}</h4>
                    
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-2">
                        <Label>Название *</Label>
                        <Input value={oppForm.title} onChange={(e) => setOppForm((prev) => ({ ...prev, title: e.target.value }))} placeholder="Junior Python Developer" />
                      </div>
                      <div className="space-y-2">
                        <Label>Тип</Label>
                        <Select value={oppForm.type} onValueChange={(v) => setOppForm((prev) => ({ ...prev, type: v as OpportunityType }))}>
                          <SelectTrigger><SelectValue /></SelectTrigger>
                          <SelectContent>
                            <SelectItem value="vacancy">Вакансия</SelectItem>
                            <SelectItem value="internship">Стажировка</SelectItem>
                            <SelectItem value="mentorship">Менторство</SelectItem>
                            <SelectItem value="event">Мероприятие</SelectItem>
                          </SelectContent>
                        </Select>
                      </div>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-2">
                        <Label>Город *</Label>
                        <Input value={oppForm.city} onChange={(e) => setOppForm((prev) => ({ ...prev, city: e.target.value }))} placeholder="Москва" />
                      </div>
                      <div className="space-y-2">
                        <Label>Формат работы</Label>
                        <Select value={oppForm.workFormat} onValueChange={(v) => setOppForm((prev) => ({ ...prev, workFormat: v as WorkFormat }))}>
                          <SelectTrigger><SelectValue /></SelectTrigger>
                          <SelectContent>
                            <SelectItem value="office">Офис</SelectItem>
                            <SelectItem value="hybrid">Гибрид</SelectItem>
                            <SelectItem value="remote">Удаленно</SelectItem>
                          </SelectContent>
                        </Select>
                      </div>
                    </div>

                    <div className="space-y-2">
                      <Label>Адрес</Label>
                      <Input value={oppForm.address} onChange={(e) => setOppForm((prev) => ({ ...prev, address: e.target.value }))} placeholder="Улица, дом" />
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                      <div className="space-y-2">
                        <Label>Зарплата от</Label>
                        <Input type="number" value={oppForm.salaryMin} onChange={(e) => setOppForm((prev) => ({ ...prev, salaryMin: parseInt(e.target.value) || 0 }))} />
                      </div>
                      <div className="space-y-2">
                        <Label>Зарплата до</Label>
                        <Input type="number" value={oppForm.salaryMax} onChange={(e) => setOppForm((prev) => ({ ...prev, salaryMax: parseInt(e.target.value) || 0 }))} />
                      </div>
                      <div className="space-y-2">
                        <Label>Валюта</Label>
                        <Select value={oppForm.currency} onValueChange={(v) => setOppForm((prev) => ({ ...prev, currency: v }))}>
                          <SelectTrigger><SelectValue /></SelectTrigger>
                          <SelectContent>
                            <SelectItem value="RUB">RUB</SelectItem>
                            <SelectItem value="USD">USD</SelectItem>
                            <SelectItem value="EUR">EUR</SelectItem>
                          </SelectContent>
                        </Select>
                      </div>
                    </div>

                    <div className="space-y-2">
                      <Label>Описание *</Label>
                      <Textarea value={oppForm.description} onChange={(e) => setOppForm((prev) => ({ ...prev, description: e.target.value }))} rows={4} />
                    </div>

                    <div className="space-y-2">
                      <Label>Требования</Label>
                      <Textarea value={oppForm.requirements} onChange={(e) => setOppForm((prev) => ({ ...prev, requirements: e.target.value }))} rows={3} />
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-2">
                        <Label>Контактный email</Label>
                        <Input value={oppForm.contactEmail} onChange={(e) => setOppForm((prev) => ({ ...prev, contactEmail: e.target.value }))} />
                      </div>
                      <div className="space-y-2">
                        <Label>Контактный телефон</Label>
                        <Input value={oppForm.contactPhone} onChange={(e) => setOppForm((prev) => ({ ...prev, contactPhone: e.target.value }))} />
                      </div>
                    </div>

                    <div className="flex gap-2">
                      <Button onClick={() => editingId ? handleUpdateOpp() : handleCreateOpp()} disabled={!oppForm.title || !oppForm.description || !oppForm.city}>
                        {editingId ? 'Сохранить' : 'Создать'}
                      </Button>
                      <Button variant="outline" onClick={resetOppForm}>Отмена</Button>
                    </div>
                  </div>
                )}

                <div className="space-y-3">
                  {isLoading ? (
                    <p className="text-gray-500">Загрузка...</p>
                  ) : filteredOpportunities.length === 0 ? (
                    <p className="text-gray-500">Нет возможностей</p>
                  ) : (
                    filteredOpportunities.map((opportunity) => (
                      <div key={opportunity.id} className="border rounded-lg p-4">
                        <div className="flex justify-between items-start">
                          <div className="flex-1">
                            <div className="flex items-center gap-2 mb-1">
                              <h4 className="font-semibold">{opportunity.title}</h4>
                              <Badge variant={opportunity.status === 'active' ? 'default' : opportunity.status === 'closed' ? 'destructive' : 'secondary'}>
                                {opportunity.status === 'active' ? 'Активно' : opportunity.status === 'closed' ? 'Закрыто' : 'Запланировано'}
                              </Badge>
                            </div>
                            <p className="text-sm text-gray-500">
                              {typeLabels[opportunity.type as OpportunityType]} • {formatLabels[opportunity.workFormat as WorkFormat]} • {opportunity.location.city}
                            </p>
                            <p className="text-sm text-gray-600 mt-2 line-clamp-2">{opportunity.description}</p>
                          </div>
                          <div className="flex gap-1 ml-2">
                            <Button variant="ghost" size="icon" onClick={() => handleEditOpp(opportunity)}>
                              <Edit className="w-4 h-4" />
                            </Button>
                            <Button variant="ghost" size="icon" onClick={() => handleDeleteOpp(opportunity.id)}>
                              <Trash2 className="w-4 h-4" />
                            </Button>
                          </div>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              </CardContent>
            </Card>
          )}

          {activeTab === 'applications' && (
            <Card>
              <CardHeader>
                <CardTitle>Отклики</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 w-4 h-4" />
                  <Input 
                    className="pl-10" 
                    placeholder="Поиск по имени или email..." 
                    value={appSearchQuery}
                    onChange={(e) => setAppSearchQuery(e.target.value)}
                  />
                </div>

                {isLoading ? (
                  <p className="text-gray-500">Загрузка...</p>
                ) : filteredApplications.length === 0 ? (
                  <p className="text-gray-500">Нет откликов</p>
                ) : (
                  <div className="space-y-3">
                    {filteredApplications.map((app) => {
                      const applicant = applicants.find((item) => item.id === app.applicantId);
                      const opportunity = opportunities.find((item) => item.id === app.opportunityId);
                      return (
                        <div key={app.id} className="border rounded-lg p-4">
                          <div className="flex justify-between items-start">
                            <div 
                              className="cursor-pointer flex-1"
                              onClick={() => navigate(`/user/${app.applicantId}`)}
                            >
                              <h4 className="font-semibold hover:text-blue-600">{applicant?.displayName || applicant?.email || 'Соискатель'}</h4>
                              {applicant?.university && <p className="text-sm text-gray-500">{applicant.university}</p>}
                              <p className="text-sm text-blue-600 mt-1">{opportunity?.title}</p>
                              {app.message && <p className="text-sm text-gray-600 mt-2 italic">"{app.message}"</p>}
                            </div>
                            <Badge className="ml-2">{statusMap[app.status]}</Badge>
                          </div>
                          <div className="flex gap-2 mt-3">
                            <Button size="sm" onClick={() => void handleStatus(app.id, 'accepted')}><CheckCircle className="w-4 h-4 mr-1" />Принять</Button>
                            <Button size="sm" variant="outline" onClick={() => void handleStatus(app.id, 'reserved')}><Clock className="w-4 h-4 mr-1" />В резерв</Button>
                            <Button size="sm" variant="destructive" onClick={() => void handleStatus(app.id, 'rejected')}><XCircle className="w-4 h-4 mr-1" />Отклонить</Button>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                )}
              </CardContent>
            </Card>
          )}

          {activeTab === 'recommendations' && (
            <Card>
              <CardHeader>
                <CardTitle>Входящие рекомендации</CardTitle>
              </CardHeader>
              <CardContent>
                {isLoading ? (
                  <p className="text-gray-500">Загрузка...</p>
                ) : recommendations.length === 0 ? (
                  <p className="text-gray-500">У вас пока нет входящих рекомендаций.</p>
                ) : (
                  <div className="space-y-4">
                    {recommendations.map(rec => (
                      <div key={rec.id} className="border rounded-lg p-4">
                        <div className="flex items-center justify-between mb-2">
                          <div className="font-semibold text-lg">
                            Рекомендация от {rec.referrer?.displayName || rec.referrer?.email}
                          </div>
                          <Badge variant="secondary">Рекомендация от друга</Badge>
                        </div>
                        
                        <div 
                          className="bg-white border rounded p-3 mt-2 cursor-pointer hover:bg-gray-50"
                          onClick={() => navigate(`/user/${rec.referee?.id}`)}
                        >
                          <h4 className="font-medium text-blue-600">{rec.referee?.displayName || rec.referee?.email}</h4>
                          <p className="text-sm text-gray-500 mt-1">Рекомендуемый кандидат</p>
                          {rec.opportunity && (
                            <p className="text-sm text-gray-500 mt-1">
                              На вакансию: <span className="font-medium">{rec.opportunity.title}</span>
                            </p>
                          )}
                        </div>
                        
                        {rec.comment && (
                          <p className="text-sm text-gray-600 mt-3 p-2 bg-gray-50 rounded border-l-2 border-blue-500 italic">
                            "{rec.comment}"
                          </p>
                        )}
                        <div className="text-xs text-gray-400 mt-2">
                          {new Date(rec.createdAt).toLocaleString()}
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </CardContent>
            </Card>
          )}
        </div>
      </div>
    </div>
  );
};
