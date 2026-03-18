import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router';
import { OpportunityMap } from '../components/OpportunityMap';
import { OpportunityCard } from '../components/OpportunityCard';
import { opportunities as allOpportunities, tags } from '../data/mockData';
import { Opportunity } from '../types';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../components/ui/select';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../components/ui/tabs';
import { Badge } from '../components/ui/badge';
import { Search, LogIn, UserPlus, Map, List, X } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { toast } from 'sonner';

export const HomePage: React.FC = () => {
  const navigate = useNavigate();
  const { currentUser } = useAuth();
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedType, setSelectedType] = useState<string>('all');
  const [selectedFormat, setSelectedFormat] = useState<string>('all');
  const [selectedTags, setSelectedTags] = useState<string[]>([]);
  const [filteredOpportunities, setFilteredOpportunities] = useState<Opportunity[]>(allOpportunities);
  const [favorites, setFavorites] = useState<string[]>([]);
  const [viewMode, setViewMode] = useState<'map' | 'list'>('map');

  // Загрузка избранного из localStorage
  useEffect(() => {
    const savedFavorites = localStorage.getItem('favorites');
    if (savedFavorites) {
      setFavorites(JSON.parse(savedFavorites));
    }
  }, []);

  // Фильтрация возможностей
  useEffect(() => {
    let filtered = allOpportunities.filter((opp) => opp.status === 'active');

    if (searchQuery) {
      filtered = filtered.filter((opp) =>
        opp.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        opp.description.toLowerCase().includes(searchQuery.toLowerCase())
      );
    }

    if (selectedType !== 'all') {
      filtered = filtered.filter((opp) => opp.type === selectedType);
    }

    if (selectedFormat !== 'all') {
      filtered = filtered.filter((opp) => opp.workFormat === selectedFormat);
    }

    if (selectedTags.length > 0) {
      filtered = filtered.filter((opp) =>
        selectedTags.every((tagId) => opp.tags.includes(tagId))
      );
    }

    setFilteredOpportunities(filtered);
  }, [searchQuery, selectedType, selectedFormat, selectedTags]);

  const toggleFavorite = (id: string) => {
    const newFavorites = favorites.includes(id)
      ? favorites.filter((fav) => fav !== id)
      : [...favorites, id];
    setFavorites(newFavorites);
    localStorage.setItem('favorites', JSON.stringify(newFavorites));
  };

  const toggleTag = (tagId: string) => {
    setSelectedTags((prev) =>
      prev.includes(tagId) ? prev.filter((id) => id !== tagId) : [...prev, tagId]
    );
  };

  const handleApply = (opportunityId: string) => {
    if (!currentUser) {
      toast.error('Необходимо войти в систему');
      return;
    }
    if (currentUser.role !== 'applicant') {
      toast.error('Только соискатели могут откликаться на вакансии');
      return;
    }
    toast.success('Отклик отправлен!');
  };

  const techTags = tags.filter((tag) => tag.category === 'technology');
  const levelTags = tags.filter((tag) => tag.category === 'level');

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
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
                    const path =
                      currentUser.role === 'applicant'
                        ? '/dashboard/applicant'
                        : currentUser.role === 'employer'
                        ? '/dashboard/employer'
                        : '/dashboard/curator';
                    navigate(path);
                  }}
                >
                  Личный кабинет
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

      {/* Filters */}
      <div className="bg-white border-b">
        <div className="container mx-auto px-4 py-4">
          <div className="space-y-4">
            {/* Search and Selects */}
            <div className="flex gap-3 flex-wrap">
              <div className="flex-1 min-w-[200px]">
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                  <Input
                    placeholder="Поиск вакансий, стажировок, мероприятий..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className="pl-10"
                  />
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
                <Button
                  variant={viewMode === 'map' ? 'default' : 'outline'}
                  size="icon"
                  onClick={() => setViewMode('map')}
                >
                  <Map className="w-4 h-4" />
                </Button>
                <Button
                  variant={viewMode === 'list' ? 'default' : 'outline'}
                  size="icon"
                  onClick={() => setViewMode('list')}
                >
                  <List className="w-4 h-4" />
                </Button>
              </div>
            </div>

            {/* Tags */}
            <div className="space-y-2">
              <div className="flex items-center gap-2">
                <span className="text-sm font-medium">Технологии:</span>
                <div className="flex flex-wrap gap-2">
                  {techTags.map((tag) => (
                    <Badge
                      key={tag.id}
                      variant={selectedTags.includes(tag.id) ? 'default' : 'outline'}
                      className="cursor-pointer"
                      onClick={() => toggleTag(tag.id)}
                    >
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
                    <Badge
                      key={tag.id}
                      variant={selectedTags.includes(tag.id) ? 'default' : 'outline'}
                      className="cursor-pointer"
                      onClick={() => toggleTag(tag.id)}
                    >
                      {tag.name}
                      {selectedTags.includes(tag.id) && <X className="w-3 h-3 ml-1" />}
                    </Badge>
                  ))}
                </div>
              </div>
            </div>

            {/* Results count */}
            <div className="text-sm text-gray-600">
              Найдено возможностей: <span className="font-semibold">{filteredOpportunities.length}</span>
            </div>
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="container mx-auto px-4 py-6">
        {viewMode === 'map' ? (
          <div className="h-[600px] rounded-lg overflow-hidden shadow-lg">
            <OpportunityMap
              opportunities={filteredOpportunities}
              favorites={favorites}
              onOpportunityClick={(opportunity) => {
                // В будущем можно открывать модальное окно с деталями
                console.log('Selected opportunity:', opportunity);
              }}
            />
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {filteredOpportunities.map((opportunity) => (
              <OpportunityCard
                key={opportunity.id}
                opportunity={opportunity}
                isFavorite={favorites.includes(opportunity.id)}
                onToggleFavorite={toggleFavorite}
                onApply={handleApply}
                isAuthenticated={!!currentUser && currentUser.role === 'applicant'}
              />
            ))}
          </div>
        )}

        {filteredOpportunities.length === 0 && (
          <div className="text-center py-12">
            <p className="text-gray-500 text-lg">Не найдено возможностей по вашему запросу</p>
            <Button
              variant="outline"
              className="mt-4"
              onClick={() => {
                setSearchQuery('');
                setSelectedType('all');
                setSelectedFormat('all');
                setSelectedTags([]);
              }}
            >
              Сбросить фильтры
            </Button>
          </div>
        )}
      </div>
    </div>
  );
};
