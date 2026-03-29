import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router';
import { useAuth } from '../context/AuthContext';
import { Application, Company, Opportunity, statusMap } from '../types';
import { Button } from '../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Textarea } from '../components/ui/textarea';
import { Badge } from '../components/ui/badge';
import { Switch } from '../components/ui/switch';
import { Avatar, AvatarFallback } from '../components/ui/avatar';
import { toast } from 'sonner';
import { LogOut, Home, User as UserIcon, Briefcase, Star, Settings, CheckCircle, Clock, XCircle, Search, UserPlus, UserMinus } from 'lucide-react';
import { appApi, Friend, type User } from '../api/appApi';
import { Recommendation } from '../types';

export const ApplicantDashboard: React.FC = () => {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const { currentUser, logout, syncUser } = useAuth();
    const [activeTab, setActiveTab] = useState<'profile' | 'applications' | 'favorites' | 'settings' | 'friends' | 'recommendations'>('profile');
    const [applications, setApplications] = useState<Application[]>([]);
    const [opportunities, setOpportunities] = useState<Opportunity[]>([]);
    const [companies, setCompanies] = useState<Company[]>([]);
    const [favorites, setFavorites] = useState<Opportunity[]>([]);
    const [friends, setFriends] = useState<Friend[]>([]);
    const [recommendations, setRecommendations] = useState<Recommendation[]>([]);
    const [recommendingAppId, setRecommendingAppId] = useState<string | null>(null);
    const [recommendFriendId, setRecommendFriendId] = useState<string>('');
    const [recommendComment, setRecommendComment] = useState<string>('');
    const [pendingRequests, setPendingRequests] = useState<Friend[]>([]);
    const [sentRequests, setSentRequests] = useState<Friend[]>([]);
    const [searchResults, setSearchResults] = useState<User[]>([]);
    const [searchQuery, setSearchQuery] = useState('');
    const [isSearching, setIsSearching] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const [profile, setProfile] = useState({
        displayName: '',
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
            displayName: currentUser.displayName || '',
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
        const tab = searchParams.get('tab');
        if (tab === 'applications' || tab === 'favorites') {
            setActiveTab(tab);
        }
    }, [searchParams]);

    useEffect(() => {
        async function load() {
            try {
                const [nextApplications, nextOpportunities, nextCompanies, nextFavorites, nextFriends, nextPending, nextSent, nextRecs] = await Promise.all([
                    appApi.getApplicantApplications(),
                    appApi.getOpportunities(),
                    appApi.getCompanies(),
                    appApi.getFavorites(),
                    appApi.getFriends(),
                    appApi.getPendingFriendRequests(),
                    appApi.getSentFriendRequests(),
                    appApi.getApplicantRecommendations(),
                ]);
                setApplications(nextApplications);
                setOpportunities(nextOpportunities);
                setCompanies(nextCompanies);
                setFavorites(nextFavorites);
                setFriends(nextFriends);
                setPendingRequests(nextPending);
                setSentRequests(nextSent);
                setRecommendations(nextRecs);
            } catch (error) {
                toast.error(error instanceof Error ? error.message : 'Ошибка при загрузке профиля соискателя');
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
                displayName: profile.displayName,
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
            toast.success('Сохранено');
        } catch (error) {
            toast.error(error instanceof Error ? error.message : 'Ошибка при сохранении профиля');
        }
    };

    const handleSavePrivacy = async () => {
        try {
            const user = await appApi.updateApplicantPrivacy(privacySettings);
            syncUser(user);
            toast.success('Настройки приватности сохранены');
        } catch (error) {
            toast.error(error instanceof Error ? error.message : 'Ошибка при сохранении настрокк приватности');
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

    const getStatusRuName = (status: string) => {
        switch (status) {
            case 'accepted':
                return 'Принят';
            case 'rejected':
                return 'Отклонен';
            case 'pending':
                return 'В обработке';
        }
    }

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
                        <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-purple-600 rounded-lg flex items-center justify-center" onClick={() => navigate('/')}>
                            <span className="text-white font-bold text-xl">T</span>
                        </div>
                        <h1 className="text-xl font-bold">Профиль соискателя</h1>
                    </div>
                    <div className="flex gap-3">
                        <Button variant="outline" onClick={() => navigate('/')}><Home className="w-4 h-4 mr-2" />На главную</Button>
                        <Button variant="outline" onClick={() => void handleLogout()}><LogOut className="w-4 h-4 mr-2" />Выход</Button>
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
                            <Button variant={activeTab === 'profile' ? 'default' : 'ghost'} className="w-full justify-start" onClick={() => setActiveTab('profile')}><UserIcon className="w-4 h-4 mr-2" />Профиль</Button>
                            <Button variant={activeTab === 'applications' ? 'default' : 'ghost'} className="w-full justify-start" onClick={() => setActiveTab('applications')}><Briefcase className="w-4 h-4 mr-2" />Отклики</Button>
                            <Button variant={activeTab === 'favorites' ? 'default' : 'ghost'} className="w-full justify-start" onClick={() => setActiveTab('favorites')}><Star className="w-4 h-4 mr-2" />Избранное</Button>
                            <Button variant={activeTab === 'friends' ? 'default' : 'ghost'} className="w-full justify-start" onClick={() => setActiveTab('friends')}><UserPlus className="w-4 h-4 mr-2" />Друзья</Button>
                            <Button variant={activeTab === 'recommendations' ? 'default' : 'ghost'} className="w-full justify-start" onClick={() => setActiveTab('recommendations')}><Star className="w-4 h-4 mr-2" />Рекомендации</Button>
                            <Button variant={activeTab === 'settings' ? 'default' : 'ghost'} className="w-full justify-start" onClick={() => setActiveTab('settings')}><Settings className="w-4 h-4 mr-2" />Настройки</Button>
                        </div>
                    </CardContent>
                </Card>

                <div className="lg:col-span-3">
                    {activeTab === 'profile' && (
                        <Card>
                            <CardHeader><CardTitle>Профиль</CardTitle></CardHeader>
                            <CardContent className="space-y-4">
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                    <div className="space-y-2"><Label>Отображаемое имя</Label><Input value={profile.displayName} onChange={(e) => setProfile((prev) => ({ ...prev, displayName: e.target.value }))} /></div>
                                    <div className="space-y-2"><Label>Полное имя</Label><Input value={profile.fullName} onChange={(e) => setProfile((prev) => ({ ...prev, fullName: e.target.value }))} /></div>
                                    <div className="space-y-2"><Label>Учебное заведение</Label><Input value={profile.university} onChange={(e) => setProfile((prev) => ({ ...prev, university: e.target.value }))} /></div>
                                    <div className="space-y-2"><Label>Курс</Label><Input value={profile.course} onChange={(e) => setProfile((prev) => ({ ...prev, course: e.target.value }))} /></div>
                                    <div className="space-y-2"><Label>Год выпуска</Label><Input value={profile.graduationYear} onChange={(e) => setProfile((prev) => ({ ...prev, graduationYear: e.target.value }))} /></div>
                                </div>
                                <div className="space-y-2"><Label>Навыки</Label><Input value={profile.skills} onChange={(e) => setProfile((prev) => ({ ...prev, skills: e.target.value }))} /></div>
                                <div className="space-y-2"><Label>Ссылка на портфолио</Label><Input value={profile.portfolioLinks} onChange={(e) => setProfile((prev) => ({ ...prev, portfolioLinks: e.target.value }))} /></div>
                                <div className="space-y-2"><Label>Контактные данные</Label><Input value={profile.contacts} onChange={(e) => setProfile((prev) => ({ ...prev, contacts: e.target.value }))} /></div>
                                <div className="space-y-2"><Label>О себе</Label><Textarea value={profile.resume} onChange={(e) => setProfile((prev) => ({ ...prev, resume: e.target.value }))} rows={6} /></div>
                                <Button onClick={() => void handleSaveProfile()}>Сохранить</Button>
                            </CardContent>
                        </Card>
                    )}

                    {activeTab === 'applications' && (
                        <Card>
                            <CardHeader><CardTitle>Отклики</CardTitle></CardHeader>
                            <CardContent>
                                {isLoading ? (
                                    <p className="text-gray-500">Загрузка...</p>
                                ) : applications.length === 0 ? (
                                    <p className="text-gray-500">Откликов нет.</p>
                                ) : (
                                    <div className="space-y-3">
                                        {applications.map((app) => {
                                            const opportunity = opportunities.find((item) => item.id === app.opportunityId);
                                            const company = companies.find((item) => item.id === opportunity?.companyId);
                                            return (
                                                <div 
                                                    key={app.id} 
                                                    className="border rounded-lg p-4 transition-colors"
                                                >
                                                    <div className="flex justify-between items-start cursor-pointer hover:bg-gray-50 p-2 -m-2 rounded" onClick={() => navigate(`/opportunity/${app.opportunityId}?from=applications`)}>
                                                        <div>
                                                            <h4 className="font-semibold">{opportunity?.title}</h4>
                                                            <p className="text-sm text-gray-500">{company?.name}</p>
                                                        </div>
                                                        <div className="flex items-center gap-2">
                                                            {getStatusIcon(app.status)}
                                                            <Badge>{statusMap[app.status]}</Badge>
                                                        </div>
                                                    </div>
                                                    {app.message && <p className="text-sm text-gray-600 mt-2">{app.message}</p>}
                                                    
                                                    {app.status === 'accepted' && (
                                                        <div className="mt-4 pt-4 border-t">
                                                            {recommendingAppId === app.id ? (
                                                                <div className="space-y-3 bg-gray-50 p-3 rounded-lg">
                                                                    <div className="font-semibold text-sm">Порекомендовать друга работодателю</div>
                                                                    {friends.length === 0 ? (
                                                                        <p className="text-sm text-gray-500">У вас пока нет друзей для рекомендации.</p>
                                                                    ) : (
                                                                        <>
                                                                            <select 
                                                                                className="w-full border p-2 rounded text-sm"
                                                                                value={recommendFriendId}
                                                                                onChange={e => setRecommendFriendId(e.target.value)}
                                                                            >
                                                                                <option value="">Выберите друга...</option>
                                                                                {friends.map(f => (
                                                                                    <option key={f.userId} value={f.userId}>{f.displayName} ({f.email})</option>
                                                                                ))}
                                                                            </select>
                                                                            <Input 
                                                                                placeholder="Добавить комментарий (необязательно)" 
                                                                                value={recommendComment} 
                                                                                onChange={e => setRecommendComment(e.target.value)} 
                                                                            />
                                                                            <div className="flex gap-2">
                                                                                <Button 
                                                                                    size="sm" 
                                                                                    disabled={!recommendFriendId}
                                                                                    onClick={() => {
                                                                                        appApi.recommendFriendToEmployer({
                                                                                            friendId: recommendFriendId,
                                                                                            opportunityId: app.opportunityId,
                                                                                            comment: recommendComment
                                                                                        }).then(() => {
                                                                                            toast.success('Рекомендация отправлена');
                                                                                            setRecommendingAppId(null);
                                                                                        }).catch(err => {
                                                                                            toast.error(err instanceof Error ? err.message : 'Ошибка при отправке');
                                                                                        });
                                                                                    }}
                                                                                >Отправить</Button>
                                                                                <Button size="sm" variant="outline" onClick={() => setRecommendingAppId(null)}>Отмена</Button>
                                                                            </div>
                                                                        </>
                                                                    )}
                                                                </div>
                                                            ) : (
                                                                <Button size="sm" variant="outline" onClick={() => {
                                                                    setRecommendingAppId(app.id);
                                                                    setRecommendFriendId('');
                                                                    setRecommendComment('');
                                                                }}>Рекомендовать друга</Button>
                                                            )}
                                                        </div>
                                                    )}
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
                            <CardHeader><CardTitle>Избранное</CardTitle></CardHeader>
                            <CardContent>
                                {isLoading ? (
                                    <p className="text-gray-500">Загрузка...</p>
                                ) : favorites.length === 0 ? (
                                    <p className="text-gray-500">Нет избранных.</p>
                                ) : (
                                    <div className="space-y-3">
                                        {favorites.map((opportunity) => {
                                            const company = companies.find((item) => item.id === opportunity.companyId);
                                            return (
                                                <div 
                                                    key={opportunity.id} 
                                                    className="border rounded-lg p-4 cursor-pointer hover:bg-gray-50 transition-colors"
                                                    onClick={() => navigate(`/opportunity/${opportunity.id}?from=favorites`)}
                                                >
                                                    <h4 className="font-semibold">{opportunity.title}</h4>
                                                    <p className="text-sm text-gray-500">{company?.name}</p>
                                                    <p className="text-sm text-gray-600 mt-2 line-clamp-2">{opportunity.description}</p>
                                                </div>
                                            );
                                        })}
                                    </div>
                                )}
                            </CardContent>
                        </Card>
                    )}

                    {activeTab === 'friends' && (
                        <Card>
                            <CardHeader><CardTitle>Друзья</CardTitle></CardHeader>
                            <CardContent className="space-y-6">
                                <div className="space-y-3">
                                    <Label>Поиск пользователей</Label>
                                    <div className="flex gap-2">
                                        <Input 
                                            value={searchQuery} 
                                            onChange={(e) => setSearchQuery(e.target.value)} 
                                            placeholder="Поиск по имени или email..." 
                                            onKeyDown={(e) => {
                                                if (e.key === 'Enter' && searchQuery.trim()) {
                                                    setIsSearching(true);
                                                    appApi.searchUsers(searchQuery).then(results => {
                                                        setSearchResults(results.filter(u => u.id !== currentUser?.id));
                                                        setIsSearching(false);
                                                    }).catch(() => setIsSearching(false));
                                                }
                                            }}
                                        />
                                        <Button 
                                            onClick={() => {
                                                setIsSearching(true);
                                                appApi.searchUsers(searchQuery).then(results => {
                                                    setSearchResults(results.filter(u => u.id !== currentUser?.id));
                                                    setIsSearching(false);
                                                }).catch(() => setIsSearching(false));
                                            }}
                                            disabled={!searchQuery.trim() || isSearching}
                                        >
                                            <Search className="w-4 h-4" />
                                        </Button>
                                    </div>
                                    
                                    {searchResults.length > 0 && (
                                        <div className="border rounded-lg p-4 space-y-2">
                                            <Label>Результаты поиска:</Label>
                                            {searchResults.map(user => {
                                                const isAlreadyFriend = friends.some(f => f.userId === user.id);
                                                const isPending = pendingRequests.some(p => p.userId === user.id);
                                                const isSent = sentRequests.some(s => s.userId === user.id);
                                                
                                                return (
                                                    <div key={user.id} className="flex justify-between items-center p-2 bg-gray-50 rounded">
                                                        <div className="cursor-pointer" onClick={() => navigate(`/user/${user.id}`)}>
                                                            <div className="font-medium">{user.displayName}</div>
                                                            <div className="text-sm text-gray-500">{user.email}</div>
                                                        </div>
                                                        <div>
                                                            {isAlreadyFriend && (
                                                                <Button variant="outline" size="sm" onClick={() => {
                                                                    appApi.removeFriend(user.id).then(() => {
                                                                        setFriends(prev => prev.filter(f => f.userId !== user.id));
                                                                    });
                                                                }}>
                                                                    <UserMinus className="w-4 h-4" />
                                                                </Button>
                                                            )}
                                                            {isPending && (
                                                                <>
                                                                    <Button size="sm" className="mr-2" onClick={() => {
                                                                        appApi.acceptFriendRequest(user.id).then(() => {
                                                                            setPendingRequests(prev => prev.filter(p => p.userId !== user.id));
                                                                            setFriends(prev => [...prev, { id: '', userId: user.id, email: user.email, displayName: user.displayName, status: 'accepted', createdAt: '' }]);
                                                                        });
                                                                    }}>
                                                                        <CheckCircle className="w-4 h-4" />
                                                                    </Button>
                                                                    <Button variant="outline" size="sm" onClick={() => {
                                                                        appApi.rejectFriendRequest(user.id).then(() => {
                                                                            setPendingRequests(prev => prev.filter(p => p.userId !== user.id));
                                                                        });
                                                                    }}>
                                                                        <XCircle className="w-4 h-4" />
                                                                    </Button>
                                                                </>
                                                            )}
                                                            {isSent && (
                                                                <Button variant="outline" size="sm" onClick={() => {
                                                                    appApi.cancelFriendRequest(user.id).then(() => {
                                                                        setSentRequests(prev => prev.filter(s => s.userId !== user.id));
                                                                    });
                                                                }}>
                                                                    Отменить
                                                                </Button>
                                                            )}
                                                            {!isAlreadyFriend && !isPending && !isSent && (
                                                                <Button size="sm" onClick={() => {
                                                                    appApi.sendFriendRequest(user.id).then(() => {
                                                                        setSentRequests(prev => [...prev, { id: '', userId: user.id, email: user.email, displayName: user.displayName, status: 'pending', createdAt: '' }]);
                                                                    });
                                                                }}>
                                                                    <UserPlus className="w-4 h-4" />
                                                                </Button>
                                                            )}
                                                        </div>
                                                    </div>
                                                );
                                            })}
                                        </div>
                                    )}
                                </div>

                                {pendingRequests.length > 0 && (
                                    <div className="space-y-2">
                                        <Label>Входящие заявки ({pendingRequests.length})</Label>
                                        {pendingRequests.map(req => (
                                            <div key={req.id} className="flex justify-between items-center p-3 border rounded-lg">
                                                <div className="cursor-pointer" onClick={() => navigate(`/user/${req.userId}`)}>
                                                    <div className="font-medium">{req.displayName}</div>
                                                    <div className="text-sm text-gray-500">{req.email}</div>
                                                </div>
                                                <div className="flex gap-2">
                                                    <Button size="sm" onClick={() => {
                                                        appApi.acceptFriendRequest(req.userId).then(() => {
                                                            setPendingRequests(prev => prev.filter(p => p.userId !== req.userId));
                                                            setFriends(prev => [...prev, req]);
                                                        });
                                                    }}>
                                                        <CheckCircle className="w-4 h-4 mr-1" />Принять
                                                    </Button>
                                                    <Button variant="outline" size="sm" onClick={() => {
                                                        appApi.rejectFriendRequest(req.userId).then(() => {
                                                            setPendingRequests(prev => prev.filter(p => p.userId !== req.userId));
                                                        });
                                                    }}>
                                                        <XCircle className="w-4 h-4 mr-1" />Отклонить
                                                    </Button>
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                )}

                                {sentRequests.length > 0 && (
                                    <div className="space-y-2">
                                        <Label>Отправленные заявки ({sentRequests.length})</Label>
                                        {sentRequests.map(req => (
                                            <div key={req.id} className="flex justify-between items-center p-3 border rounded-lg">
                                                <div className="cursor-pointer" onClick={() => navigate(`/user/${req.userId}`)}>
                                                    <div className="font-medium">{req.displayName}</div>
                                                    <div className="text-sm text-gray-500">{req.email}</div>
                                                </div>
                                                <Button variant="outline" size="sm" onClick={() => {
                                                    appApi.cancelFriendRequest(req.userId).then(() => {
                                                        setSentRequests(prev => prev.filter(s => s.userId !== req.userId));
                                                    });
                                                }}>
                                                    Отменить
                                                </Button>
                                            </div>
                                        ))}
                                    </div>
                                )}

                                {friends.length > 0 ? (
                                    <div className="space-y-2">
                                        <Label>Мои друзья ({friends.length})</Label>
                                        {friends.map(friend => (
                                            <div 
                                                key={friend.id} 
                                                className="flex justify-between items-center p-3 border rounded-lg cursor-pointer hover:bg-gray-50"
                                                onClick={() => navigate(`/user/${friend.userId}`)}
                                            >
                                                <div>
                                                    <div className="font-medium">{friend.displayName}</div>
                                                    <div className="text-sm text-gray-500">{friend.email}</div>
                                                    {friend.university && <div className="text-sm text-gray-400">{friend.university}</div>}
                                                </div>
                                                <Button variant="ghost" size="sm" onClick={(e) => {
                                                    e.stopPropagation();
                                                    appApi.removeFriend(friend.userId).then(() => {
                                                        setFriends(prev => prev.filter(f => f.userId !== friend.userId));
                                                    });
                                                }}>
                                                    <UserMinus className="w-4 h-4" />
                                                </Button>
                                            </div>
                                        ))}
                                    </div>
                                ) : (
                                    <p className="text-gray-500 text-center py-4">У вас пока нет друзей</p>
                                )}
                            </CardContent>
                        </Card>
                    )}

                    {activeTab === 'recommendations' && (
                        <Card>
                            <CardHeader><CardTitle>Входящие рекомендации</CardTitle></CardHeader>
                            <CardContent>
                                {isLoading ? (
                                    <p className="text-gray-500">Загрузка...</p>
                                ) : recommendations.length === 0 ? (
                                    <p className="text-gray-500">У вас пока нет входящих рекомендаций.</p>
                                ) : (
                                    <div className="space-y-3">
                                        {recommendations.map(rec => {
                                            // APPLICANT recommendations are about an EMPLOYER. So subjectUser is an employer, or maybe opportunity is provided.
                                            // The backend endpoint `recommendEmployerToFriend` sets subjectUser to the EMPLOYER user. 
                                            // We have the company information. Wait, subjectUser has companyId. We can find the company.
                                            const recommendedCompany = rec.subjectUser?.companyId 
                                                ? companies.find(c => c.id === rec.subjectUser!.companyId) 
                                                : undefined;
                                                
                                            const recommendedOpportunity = rec.opportunity;
                                                
                                            return (
                                                <div key={rec.id} className="border rounded-lg p-4">
                                                    <div className="flex items-center justify-between mb-2">
                                                        <div className="font-semibold text-lg">
                                                            Рекомендация от {rec.referrer?.displayName || rec.referrer?.email}
                                                        </div>
                                                        <Badge variant="secondary">Рекомендация</Badge>
                                                    </div>
                                                    {recommendedOpportunity ? (
                                                        <div 
                                                            className="bg-white border rounded p-3 mt-2 cursor-pointer hover:bg-gray-50"
                                                            onClick={() => navigate(`/opportunity/${recommendedOpportunity.id}`)}
                                                        >
                                                            <h4 className="font-medium text-blue-600">{recommendedOpportunity.title}</h4>
                                                            <p className="text-sm text-gray-500">
                                                                {companies.find(c => c.id === recommendedOpportunity.companyId)?.name || 'Компания'}
                                                            </p>
                                                            <p className="text-sm text-gray-500 mt-1 line-clamp-2">Вам порекомендовали эту возможность.</p>
                                                        </div>
                                                    ) : recommendedCompany ? (
                                                        <div 
                                                            className="bg-white border rounded p-3 mt-2 cursor-pointer hover:bg-gray-50"
                                                            onClick={() => navigate(`/company/${recommendedCompany.id}`)}
                                                        >
                                                            <h4 className="font-medium text-blue-600">{recommendedCompany.name}</h4>
                                                            {recommendedCompany.address && <p className="text-sm text-gray-500">{recommendedCompany.address}</p>}
                                                            <p className="text-sm text-gray-500 mt-1 line-clamp-2">Вам порекомендовали этого работодателя.</p>
                                                        </div>
                                                    ) : rec.subjectUser ? (
                                                        <div className="bg-white border rounded p-3 mt-2">
                                                            <h4 className="font-medium">{rec.subjectUser.displayName}</h4>
                                                            <p className="text-sm text-gray-500">{rec.subjectUser.email}</p>
                                                        </div>
                                                    ) : null}
                                                    {rec.comment && (
                                                        <p className="text-sm text-gray-600 mt-3 p-2 bg-gray-50 rounded border-l-2 border-blue-500 italic">
                                                            "{rec.comment}"
                                                        </p>
                                                    )}
                                                    <div className="text-xs text-gray-400 mt-2">
                                                        {new Date(rec.createdAt).toLocaleString()}
                                                    </div>
                                                </div>
                                            )
                                        })}
                                    </div>
                                )}
                            </CardContent>
                        </Card>
                    )}

                    {activeTab === 'settings' && (
                        <Card>
                            <CardHeader><CardTitle>Настройки</CardTitle></CardHeader>
                            <CardContent className="space-y-4">
                                <div className="flex items-center justify-between">
                                    <Label>Показывать отклики в профиле</Label>
                                    <Switch checked={privacySettings.showApplications} onCheckedChange={(checked) => setPrivacySettings((prev) => ({ ...prev, showApplications: checked }))} />
                                </div>
                                <div className="flex items-center justify-between">
                                    <Label>Показывать "О себе" в профиле</Label>
                                    <Switch checked={privacySettings.showResume} onCheckedChange={(checked) => setPrivacySettings((prev) => ({ ...prev, showResume: checked }))} />
                                </div>
                                <Button onClick={() => void handleSavePrivacy()}>Сохранить</Button>
                            </CardContent>
                        </Card>
                    )}
                </div>
            </div>
        </div>
    );
};
