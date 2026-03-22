import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { useAuth } from '../context/AuthContext';
import { Application, Company, Opportunity, User } from '../types';
import { Button } from '../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Textarea } from '../components/ui/textarea';
import { Badge } from '../components/ui/badge';
import { toast } from 'sonner';
import { LogOut, Home, Building2, Briefcase, Users, CheckCircle, Clock, XCircle } from 'lucide-react';
import { appApi } from '../api/appApi';

export const EmployerDashboard: React.FC = () => {
  const navigate = useNavigate();
  const { currentUser, logout } = useAuth();
  const [activeTab, setActiveTab] = useState<'company' | 'opportunities' | 'applications'>('company');
  const [companies, setCompanies] = useState<Company[]>([]);
  const [opportunities, setOpportunities] = useState<Opportunity[]>([]);
  const [applications, setApplications] = useState<Application[]>([]);
  const [applicants, setApplicants] = useState<User[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [form, setForm] = useState({
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
    requirements: '',
  });

  const company = companies.find((item) => item.id === currentUser?.companyId);

  useEffect(() => {
    async function load() {
      try {
        const [nextCompanies, nextOpportunities, nextApplications, nextApplicants] = await Promise.all([
          appApi.getCompanies(),
          appApi.getEmployerOpportunities(),
          appApi.getEmployerApplications(),
          appApi.getEmployerApplicants(),
        ]);
        setCompanies(nextCompanies);
        setOpportunities(nextOpportunities);
        setApplications(nextApplications);
        setApplicants(nextApplicants);
      } catch (error) {
        toast.error(error instanceof Error ? error.message : 'Failed to load employer data');
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

  const handleCreate = async () => {
    try {
      const created = await appApi.createEmployerOpportunity(form);
      setOpportunities((prev) => [created, ...prev]);
      toast.success('Opportunity created');
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Failed to create opportunity');
    }
  };

  const handleStatus = async (applicationId: string, status: string) => {
    try {
      await appApi.updateEmployerApplicationStatus(applicationId, status);
      setApplications((prev) => prev.map((item) => (item.id === applicationId ? { ...item, status: status as Application['status'] } : item)));
      toast.success('Application updated');
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Failed to update application');
    }
  };

  if (!currentUser || currentUser.role !== 'employer') {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Card>
          <CardContent className="pt-6">
            <Button onClick={() => navigate('/')}>Back home</Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white border-b">
        <div className="container mx-auto px-4 py-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-purple-600 rounded-lg flex items-center justify-center">
              <span className="text-white font-bold text-xl">T</span>
            </div>
            <h1 className="text-xl font-bold">Employer Dashboard</h1>
          </div>
          <div className="flex gap-3">
            <Button variant="outline" onClick={() => navigate('/')}><Home className="w-4 h-4 mr-2" />Home</Button>
            <Button variant="outline" onClick={() => void handleLogout()}><LogOut className="w-4 h-4 mr-2" />Logout</Button>
          </div>
        </div>
      </header>

      <div className="container mx-auto px-4 py-6 grid grid-cols-1 lg:grid-cols-4 gap-6">
        <Card className="lg:col-span-1">
          <CardContent className="pt-6 space-y-2">
            <Button variant={activeTab === 'company' ? 'default' : 'ghost'} className="w-full justify-start" onClick={() => setActiveTab('company')}><Building2 className="w-4 h-4 mr-2" />Company</Button>
            <Button variant={activeTab === 'opportunities' ? 'default' : 'ghost'} className="w-full justify-start" onClick={() => setActiveTab('opportunities')}><Briefcase className="w-4 h-4 mr-2" />Opportunities</Button>
            <Button variant={activeTab === 'applications' ? 'default' : 'ghost'} className="w-full justify-start" onClick={() => setActiveTab('applications')}><Users className="w-4 h-4 mr-2" />Applications</Button>
          </CardContent>
        </Card>

        <div className="lg:col-span-3">
          {activeTab === 'company' && (
            <Card>
              <CardHeader><CardTitle>Company</CardTitle></CardHeader>
              <CardContent className="space-y-3">
                <Input value={company?.name || ''} disabled />
                <Input value={company?.industry || ''} disabled />
                <Textarea value={company?.description || ''} disabled rows={4} />
                <Badge variant={company?.verified ? 'default' : 'secondary'}>{company?.verified ? 'verified' : 'pending'}</Badge>
              </CardContent>
            </Card>
          )}

          {activeTab === 'opportunities' && (
            <Card>
              <CardHeader><CardTitle>Opportunities</CardTitle></CardHeader>
              <CardContent className="space-y-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-2"><Label>Title</Label><Input value={form.title} onChange={(e) => setForm((prev) => ({ ...prev, title: e.target.value }))} /></div>
                  <div className="space-y-2"><Label>City</Label><Input value={form.city} onChange={(e) => setForm((prev) => ({ ...prev, city: e.target.value }))} /></div>
                </div>
                <div className="space-y-2"><Label>Description</Label><Textarea value={form.description} onChange={(e) => setForm((prev) => ({ ...prev, description: e.target.value }))} rows={4} /></div>
                <div className="space-y-2"><Label>Requirements</Label><Textarea value={form.requirements} onChange={(e) => setForm((prev) => ({ ...prev, requirements: e.target.value }))} rows={3} /></div>
                <Button onClick={() => void handleCreate()} disabled={!company?.verified}>Create</Button>
                <div className="space-y-3">
                  {isLoading ? (
                    <p className="text-gray-500">Loading...</p>
                  ) : opportunities.map((opportunity) => (
                    <div key={opportunity.id} className="border rounded-lg p-4">
                      <h4 className="font-semibold">{opportunity.title}</h4>
                      <p className="text-sm text-gray-500">{opportunity.location.city}</p>
                      <p className="text-sm text-gray-600 mt-2">{opportunity.description}</p>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          )}

          {activeTab === 'applications' && (
            <Card>
              <CardHeader><CardTitle>Applications</CardTitle></CardHeader>
              <CardContent>
                {isLoading ? (
                  <p className="text-gray-500">Loading...</p>
                ) : (
                  <div className="space-y-3">
                    {applications.map((app) => {
                      const applicant = applicants.find((item) => item.id === app.applicantId);
                      const opportunity = opportunities.find((item) => item.id === app.opportunityId);
                      return (
                        <div key={app.id} className="border rounded-lg p-4">
                          <div className="flex justify-between items-start">
                            <div>
                              <h4 className="font-semibold">{applicant?.displayName || applicant?.email}</h4>
                              <p className="text-sm text-gray-500">{opportunity?.title}</p>
                            </div>
                            <Badge>{app.status}</Badge>
                          </div>
                          <div className="flex gap-2 mt-3">
                            <Button size="sm" onClick={() => void handleStatus(app.id, 'accepted')}><CheckCircle className="w-4 h-4 mr-1" />Accept</Button>
                            <Button size="sm" variant="outline" onClick={() => void handleStatus(app.id, 'reserved')}><Clock className="w-4 h-4 mr-1" />Reserve</Button>
                            <Button size="sm" variant="destructive" onClick={() => void handleStatus(app.id, 'rejected')}><XCircle className="w-4 h-4 mr-1" />Reject</Button>
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
  );
};
