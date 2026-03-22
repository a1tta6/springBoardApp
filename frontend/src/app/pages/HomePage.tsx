import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { OpportunityMap } from '../components/OpportunityMap';
import { OpportunityCard } from '../components/OpportunityCard';
import { Company, Opportunity, Tag } from '../types';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../components/ui/select';
import { Badge } from '../components/ui/badge';
import { Search, LogIn, UserPlus, Map, List, X } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { toast } from 'sonner';
import { appApi } from '../api/appApi';

export const HomePage: React.FC = () => {
  const navigate = useNavigate();
  const { currentUser, isReady } = useAuth();
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedType, setSelectedType] = useState<string>('all');
  const [selectedFormat, setSelectedFormat] = useState<string>('all');
  const [selectedTags, setSelectedTags] = useState<string[]>([]);
  const [opportunities, setOpportunities] = useState<Opportunity[]>([]);
  const [companies, setCompanies] = useState<Company[]>([]);
  const [tags, setTags] = useState<Tag[]>([]);
  const [favorites, setFavorites] = useState<string[]>([]);
  const [filteredOpportunities, setFilteredOpportunities] = useState<Opportunity[]>([]);
  const [viewMode, setViewMode] = useState<'map' | 'list'>('map');
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function load() {
      try {
        const [nextTags, nextCompanies, nextOpportunities] = await Promise.all([
          appApi.getTags(),
          appApi.getCompanies(),
          appApi.getOpportunities(),
        ]);
        setTags(nextTags);
        setCompanies(nextCompanies);
        setOpportunities(nextOpportunities);
      } catch (error) {
        toast.error(error instanceof Error ? error.message : 'Не удалось загрузить каталог');
      } finally {
        setIsLoading(false);
      }
    }

    void load();
  }, []);

  useEffect(() => {
    async function loadFavorites() {
      if (currentUser?.role !== 'applicant') {
        setFavorites([]);
        return;
      }
      try {
        const items = await appApi.getFavorites();
        setFavorites(items.map((item) => item.id));
      } catch {
        setFavorites([]);
      }
    }

    if (isReady) {
      void loadFavorites();
    }
  }, [currentUser, isReady]);

  useEffect(() => {
    let filtered = opportunities.filter((item) => item.status === 'active');

    if (searchQuery) {
      const query = searchQuery.toLowerCase();
      filtered = filtered.filter((item) => item.title.toLowerCase().includes(query) || item.description.toLowerCase().includes(query));
    }

    if (selectedType !== 'all') {
      filtered = filtered.filter((item) => item.type === selectedType);
    }

    if (selectedFormat !== 'all') {
      filtered = filtered.filter((item) => item.workFormat === selectedFormat);
    }

    if (selectedTags.length > 0) {
      filtered = filtered.filter((item) => selectedTags.every((tagId) => item.tags.includes(tagId)));
    }

    setFilteredOpportunities(filtered);
  }, [opportunities, searchQuery, selectedType, selectedFormat, selectedTags]);

  const toggleFavorite = async (id: string) => {
    if (currentUser?.role !== 'applicant') {
      toast.error('Добавлять в избранное могут только соискател');
      return;
    }

    try {
      if (favorites.includes(id)) {
        await appApi.removeFavorite(id);
        setFavorites((prev) => prev.filter((item) => item !== id));
      } else {
        await appApi.addFavorite(id);
        setFavorites((prev) => [...prev, id]);
      }
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Не удалось обновить избранное');
    }
  };

  const handleApply = async (opportunityId: string) => {
    if (!currentUser) {
      toast.error('Необходимо войти в систему');
      return;
    }
    if (currentUser.role !== 'applicant') {
      toast.error('Только соискатели могут откликаться на вакансии');
      return;
    }

    try {
      await appApi.applyToOpportunity(opportunityId);
      toast.success('Откик отправлен!');
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Не удалось откликнуться');
    }
  };

  const toggleTag = (tagId: string) => {
    setSelectedTags((prev) => (prev.includes(tagId) ? prev.filter((id) => id !== tagId) : [...prev, tagId]));
  };

  const techTags = tags.filter((tag) => tag.category === 'technology');
  const levelTags = tags.filter((tag) => tag.category === 'level');

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white border-b sticky top-0 z-10">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-purple-600 rounded-lg flex items-center justify-center">
                <span className="text-white font-bold text-xl">Т</span>
              </div>
              <h1 className="text-2xl font-bold">Трамплин</h1>
            </div>

            <div className="flex items-center gap-3">
              {currentUser ? (
                <Button
                  onClick={() => {
                    const path = currentUser.role === 'applicant' ? '/dashboard/applicant' : currentUser.role === 'employer' ? '/dashboard/employer' : '/dashboard/curator';
                    navigate(path);
                  }}
                >
                  Р›РёС‡РЅС‹Р№ РєР°Р±РёРЅРµС‚
                </Button>
              ) : (
                <>
                  <Button variant="outline" onClick={() => navigate('/login')}>
                    <LogIn className="w-4 h-4 mr-2" />
                    Войти
                  </Button>
                  <Button onClick={() => navigate('/register')}>
                    <UserPlus className="w-4 h-4 mr-2" />
                    Регистрация
                  </Button>
                </>
              )}
            </div>
          </div>
        </div>
      </header>

      <div className="bg-white border-b">
        <div className="container mx-auto px-4 py-4">
          <div className="space-y-4">
            <div className="flex gap-3 flex-wrap">
              <div className="flex-1 min-w-[200px]">
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                  <Input value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)} className="pl-10" placeholder="Поиск..." />
                </div>
              </div>

              <Select value={selectedType} onValueChange={setSelectedType}>
                <SelectTrigger className="w-[180px]">
                  <SelectValue placeholder="Тип" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">Все типы</SelectItem>
                  <SelectItem value="internship">Стажировка</SelectItem>
                  <SelectItem value="vacancy">Вакансия</SelectItem>
                  <SelectItem value="mentorship">Менторство</SelectItem>
                  <SelectItem value="event">Мероприятие</SelectItem>
                </SelectContent>
              </Select>

              <Select value={selectedFormat} onValueChange={setSelectedFormat}>
                <SelectTrigger className="w-[180px]">
                  <SelectValue placeholder="Формат работы" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">Все форматы</SelectItem>
                  <SelectItem value="office">Офис</SelectItem>
                  <SelectItem value="hybrid">Гибрид</SelectItem>
                  <SelectItem value="remote">Удаленно</SelectItem>
                </SelectContent>
              </Select>

              <div className="flex gap-2">
                <Button variant={viewMode === 'map' ? 'default' : 'outline'} size="icon" onClick={() => setViewMode('map')}>
                  <Map className="w-4 h-4" />
                </Button>
                <Button variant={viewMode === 'list' ? 'default' : 'outline'} size="icon" onClick={() => setViewMode('list')}>
                  <List className="w-4 h-4" />
                </Button>
              </div>
            </div>

            <div className="space-y-2">
              <div className="flex items-center gap-2">
                <span className="text-sm font-medium">Технологии:</span>
                <div className="flex flex-wrap gap-2">
                  {techTags.map((tag) => (
                    <Badge key={tag.id} variant={selectedTags.includes(tag.id) ? 'default' : 'outline'} className="cursor-pointer" onClick={() => toggleTag(tag.id)}>
                      {tag.name}
                      {selectedTags.includes(tag.id) && <X className="w-3 h-3 ml-1" />}
                    </Badge>
                  ))}
                </div>
              </div>

              <div className="flex items-center gap-2">
                <span className="text-sm font-medium">Уровень:</span>
                <div className="flex flex-wrap gap-2">
                  {levelTags.map((tag) => (
                    <Badge key={tag.id} variant={selectedTags.includes(tag.id) ? 'default' : 'outline'} className="cursor-pointer" onClick={() => toggleTag(tag.id)}>
                      {tag.name}
                      {selectedTags.includes(tag.id) && <X className="w-3 h-3 ml-1" />}
                    </Badge>
                  ))}
                </div>
              </div>
            </div>

            <div className="text-sm text-gray-600">
              Найдено возможностей: <span className="font-semibold">{filteredOpportunities.length}</span>
            </div>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-6">
        {isLoading ? (
          <div className="text-center py-12 text-gray-500">Загрузка каталога...</div>
        ) : viewMode === 'map' ? (
          <div className="h-[600px] rounded-lg overflow-hidden shadow-lg">
            <OpportunityMap opportunities={filteredOpportunities} companies={companies} favorites={favorites} />
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {filteredOpportunities.map((opportunity) => (
              <OpportunityCard
                key={opportunity.id}
                opportunity={opportunity}
                companies={companies}
                tags={tags}
                isFavorite={favorites.includes(opportunity.id)}
                onToggleFavorite={toggleFavorite}
                onApply={handleApply}
                isAuthenticated={!!currentUser && currentUser.role === 'applicant'}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
