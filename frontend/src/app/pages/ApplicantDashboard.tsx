import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { useAuth } from '../context/AuthContext';
import { Application, Company, Opportunity } from '../types';
import { Button } from '../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Textarea } from '../components/ui/textarea';
import { Badge } from '../components/ui/badge';
import { Switch } from '../components/ui/switch';
import { Avatar, AvatarFallback } from '../components/ui/avatar';
import { toast } from 'sonner';
import { LogOut, Home, User, Briefcase, Star, Settings, CheckCircle, Clock, XCircle } from 'lucide-react';
import { appApi } from '../api/appApi';

export const ApplicantDashboard: React.FC = () => {
  const navigate = useNavigate();
  const { currentUser, logout, syncUser } = useAuth();
  const [activeTab, setActiveTab] = useState<'profile' | 'applications' | 'favorites' | 'settings'>('profile');
  const [applications, setApplications] = useState<Application[]>([]);
  const [opportunities, setOpportunities] = useState<Opportunity[]>([]);
  const [companies, setCompanies] = useState<Company[]>([]);
  const [favorites, setFavorites] = useState<Opportunity[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [profile, setProfile] = useState({
    fullName: '',
    university: '',
    course: '',
    graduationYear: '',
    skills: '',
    portfolioLinks: '',
    resume: '',
    contacts: '',
  });
  const [privacySettings, setPrivacySettings] = useState({
    showApplications: false,
    showResume: true,
  });

  useEffect(() => {
    if (!currentUser) {
      return;
    }
    setProfile({
      fullName: currentUser.fullName || '',
      university: currentUser.university || '',
      course: currentUser.course || '',
      graduationYear: currentUser.graduationYear || '',
      skills: (currentUser.skills || []).join(', '),
      portfolioLinks: (currentUser.portfolioLinks || []).join(', '),
      resume: currentUser.resume || '',
      contacts: (currentUser.contacts || []).join(', '),
    });
    setPrivacySettings({
      showApplications: currentUser.privacySettings?.showApplications || false,
      showResume: currentUser.privacySettings?.showResume ?? true,
    });
  }, [currentUser]);

  useEffect(() => {
    async function load() {
      try {
        const [nextApplications, nextOpportunities, nextCompanies, nextFavorites] = await Promise.all([
          appApi.getApplicantApplications(),
          appApi.getOpportunities(),
          appApi.getCompanies(),
          appApi.getFavorites(),
        ]);
        setApplications(nextApplications);
        setOpportunities(nextOpportunities);
        setCompanies(nextCompanies);
        setFavorites(nextFavorites);
      } catch (error) {
        toast.error(error instanceof Error ? error.message : 'Failed to load applicant data');
      } finally {
        setIsLoading(false);
      }
    }

    if (currentUser?.role === 'applicant') {
      void load();
    }
  }, [currentUser]);

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  const handleSaveProfile = async () => {
    try {
      const user = await appApi.updateApplicantProfile({
        fullName: profile.fullName,
        university: profile.university,
        course: profile.course,
        graduationYear: profile.graduationYear,
        skills: profile.skills.split(',').map((item) => item.trim()).filter(Boolean),
        portfolioLinks: profile.portfolioLinks.split(',').map((item) => item.trim()).filter(Boolean),
        resume: profile.resume,
        contacts: profile.contacts.split(',').map((item) => item.trim()).filter(Boolean),
      });
      syncUser(user);
      toast.success('Profile saved');
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Failed to save profile');
    }
  };

  const handleSavePrivacy = async () => {
    try {
      const user = await appApi.updateApplicantPrivacy(privacySettings);
      syncUser(user);
      toast.success('Privacy settings saved');
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Failed to save privacy settings');
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'accepted':
        return <CheckCircle className="w-4 h-4 text-green-500" />;
      case 'rejected':
        return <XCircle className="w-4 h-4 text-red-500" />;
      default:
        return <Clock className="w-4 h-4 text-yellow-500" />;
    }
  };

  if (!currentUser || currentUser.role !== 'applicant') {
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
            <h1 className="text-xl font-bold">Applicant Dashboard</h1>
          </div>
          <div className="flex gap-3">
            <Button variant="outline" onClick={() => navigate('/')}><Home className="w-4 h-4 mr-2" />Home</Button>
            <Button variant="outline" onClick={() => void handleLogout()}><LogOut className="w-4 h-4 mr-2" />Logout</Button>
          </div>
        </div>
      </header>

      <div className="container mx-auto px-4 py-6 grid grid-cols-1 lg:grid-cols-4 gap-6">
        <Card className="lg:col-span-1">
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
              <Button variant={activeTab === 'profile' ? 'default' : 'ghost'} className="w-full justify-start" onClick={() => setActiveTab('profile')}><User className="w-4 h-4 mr-2" />Profile</Button>
              <Button variant={activeTab === 'applications' ? 'default' : 'ghost'} className="w-full justify-start" onClick={() => setActiveTab('applications')}><Briefcase className="w-4 h-4 mr-2" />Applications</Button>
              <Button variant={activeTab === 'favorites' ? 'default' : 'ghost'} className="w-full justify-start" onClick={() => setActiveTab('favorites')}><Star className="w-4 h-4 mr-2" />Favorites</Button>
              <Button variant={activeTab === 'settings' ? 'default' : 'ghost'} className="w-full justify-start" onClick={() => setActiveTab('settings')}><Settings className="w-4 h-4 mr-2" />Settings</Button>
            </div>
          </CardContent>
        </Card>

        <div className="lg:col-span-3">
          {activeTab === 'profile' && (
            <Card>
              <CardHeader><CardTitle>Profile</CardTitle></CardHeader>
              <CardContent className="space-y-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-2"><Label>Full name</Label><Input value={profile.fullName} onChange={(e) => setProfile((prev) => ({ ...prev, fullName: e.target.value }))} /></div>
                  <div className="space-y-2"><Label>University</Label><Input value={profile.university} onChange={(e) => setProfile((prev) => ({ ...prev, university: e.target.value }))} /></div>
                  <div className="space-y-2"><Label>Course</Label><Input value={profile.course} onChange={(e) => setProfile((prev) => ({ ...prev, course: e.target.value }))} /></div>
                  <div className="space-y-2"><Label>Graduation year</Label><Input value={profile.graduationYear} onChange={(e) => setProfile((prev) => ({ ...prev, graduationYear: e.target.value }))} /></div>
                </div>
                <div className="space-y-2"><Label>Skills</Label><Input value={profile.skills} onChange={(e) => setProfile((prev) => ({ ...prev, skills: e.target.value }))} /></div>
                <div className="space-y-2"><Label>Portfolio links</Label><Input value={profile.portfolioLinks} onChange={(e) => setProfile((prev) => ({ ...prev, portfolioLinks: e.target.value }))} /></div>
                <div className="space-y-2"><Label>Contacts</Label><Input value={profile.contacts} onChange={(e) => setProfile((prev) => ({ ...prev, contacts: e.target.value }))} /></div>
                <div className="space-y-2"><Label>Resume</Label><Textarea value={profile.resume} onChange={(e) => setProfile((prev) => ({ ...prev, resume: e.target.value }))} rows={6} /></div>
                <Button onClick={() => void handleSaveProfile()}>Save</Button>
              </CardContent>
            </Card>
          )}

          {activeTab === 'applications' && (
            <Card>
              <CardHeader><CardTitle>Applications</CardTitle></CardHeader>
              <CardContent>
                {isLoading ? (
                  <p className="text-gray-500">Loading...</p>
                ) : applications.length === 0 ? (
                  <p className="text-gray-500">No applications yet.</p>
                ) : (
                  <div className="space-y-3">
                    {applications.map((app) => {
                      const opportunity = opportunities.find((item) => item.id === app.opportunityId);
                      const company = companies.find((item) => item.id === opportunity?.companyId);
                      return (
                        <div key={app.id} className="border rounded-lg p-4">
                          <div className="flex justify-between items-start">
                            <div>
                              <h4 className="font-semibold">{opportunity?.title}</h4>
                              <p className="text-sm text-gray-500">{company?.name}</p>
                            </div>
                            <div className="flex items-center gap-2">
                              {getStatusIcon(app.status)}
                              <Badge>{app.status}</Badge>
                            </div>
                          </div>
                          {app.message && <p className="text-sm text-gray-600 mt-2">{app.message}</p>}
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
              <CardHeader><CardTitle>Favorites</CardTitle></CardHeader>
              <CardContent>
                {isLoading ? (
                  <p className="text-gray-500">Loading...</p>
                ) : favorites.length === 0 ? (
                  <p className="text-gray-500">No favorites yet.</p>
                ) : (
                  <div className="space-y-3">
                    {favorites.map((opportunity) => {
                      const company = companies.find((item) => item.id === opportunity.companyId);
                      return (
                        <div key={opportunity.id} className="border rounded-lg p-4">
                          <h4 className="font-semibold">{opportunity.title}</h4>
                          <p className="text-sm text-gray-500">{company?.name}</p>
                          <p className="text-sm text-gray-600 mt-2">{opportunity.description}</p>
                        </div>
                      );
                    })}
                  </div>
                )}
              </CardContent>
            </Card>
          )}

          {activeTab === 'settings' && (
            <Card>
              <CardHeader><CardTitle>Settings</CardTitle></CardHeader>
              <CardContent className="space-y-4">
                <div className="flex items-center justify-between">
                  <Label>Show applications</Label>
                  <Switch checked={privacySettings.showApplications} onCheckedChange={(checked) => setPrivacySettings((prev) => ({ ...prev, showApplications: checked }))} />
                </div>
                <div className="flex items-center justify-between">
                  <Label>Show resume</Label>
                  <Switch checked={privacySettings.showResume} onCheckedChange={(checked) => setPrivacySettings((prev) => ({ ...prev, showResume: checked }))} />
                </div>
                <Button onClick={() => void handleSavePrivacy()}>Save</Button>
              </CardContent>
            </Card>
          )}
        </div>
      </div>
    </div>
  );
};
