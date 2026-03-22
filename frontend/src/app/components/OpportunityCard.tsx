import React from 'react';
import { Company, Opportunity, Tag } from '../types';
import { Card, CardHeader, CardTitle, CardDescription, CardContent, CardFooter } from './ui/card';
import { Badge } from './ui/badge';
import { Button } from './ui/button';
import { MapPin, Building2, Calendar, Banknote, Briefcase, Star } from 'lucide-react';

interface OpportunityCardProps {
  opportunity: Opportunity;
  companies: Company[];
  tags: Tag[];
  isFavorite?: boolean;
  onToggleFavorite?: (id: string) => void;
  onApply?: (id: string) => void;
  isAuthenticated?: boolean;
}

const opportunityTypeLabels: Record<string, string> = {
  internship: 'Стажировка',
  vacancy: 'Вакансия',
  mentorship: 'Менторство',
  event: 'Мероприятие',
};

const workFormatLabels: Record<string, string> = {
  office: 'Офис',
  hybrid: 'Гибрид',
  remote: 'Удаленно',
};

export const OpportunityCard: React.FC<OpportunityCardProps> = ({
  opportunity,
  companies,
  tags,
  isFavorite = false,
  onToggleFavorite,
  onApply,
  isAuthenticated = false,
}) => {
  const company = companies.find((item) => item.id === opportunity.companyId);
  const opportunityTags = tags.filter((tag) => opportunity.tags.includes(tag.id));

  return (
    <Card className="hover:shadow-lg transition-shadow">
      <CardHeader>
        <div className="flex justify-between items-start">
          <div className="flex-1">
            <CardTitle className="mb-1">{opportunity.title}</CardTitle>
            <CardDescription className="flex items-center gap-2">
              <Building2 className="w-4 h-4" />
              {company?.name}
            </CardDescription>
          </div>
          {onToggleFavorite && (
            <Button
              variant="ghost"
              size="icon"
              onClick={() => onToggleFavorite(opportunity.id)}
              className={isFavorite ? 'text-yellow-500' : 'text-gray-400'}
            >
              <Star className={`w-5 h-5 ${isFavorite ? 'fill-current' : ''}`} />
            </Button>
          )}
        </div>
      </CardHeader>

      <CardContent className="space-y-3">
        <div className="flex flex-wrap gap-2">
          <Badge variant="default">{opportunityTypeLabels[opportunity.type]}</Badge>
          <Badge variant="outline">{workFormatLabels[opportunity.workFormat]}</Badge>
        </div>

        <p className="text-sm text-gray-600 line-clamp-3">{opportunity.description}</p>

        <div className="space-y-2">
          <div className="flex items-center gap-2 text-sm">
            <MapPin className="w-4 h-4 text-gray-500" />
            <span>{opportunity.location.address || opportunity.location.city}</span>
          </div>

          {opportunity.salary && (
            <div className="flex items-center gap-2 text-sm">
              <Banknote className="w-4 h-4 text-gray-500" />
              <span>
                {opportunity.salary.min?.toLocaleString()} - {opportunity.salary.max?.toLocaleString()} {opportunity.salary.currency}
              </span>
            </div>
          )}

          {opportunity.eventDate && (
            <div className="flex items-center gap-2 text-sm">
              <Calendar className="w-4 h-4 text-gray-500" />
              <span>{new Date(opportunity.eventDate).toLocaleDateString('ru-RU')}</span>
            </div>
          )}

          <div className="flex items-center gap-2 text-sm">
            <Briefcase className="w-4 h-4 text-gray-500" />
            <span>Опубликовано: {new Date(opportunity.publishedDate).toLocaleDateString('ru-RU')}</span>
          </div>
        </div>

        {opportunityTags.length > 0 && (
          <div className="flex flex-wrap gap-2">
            {opportunityTags.map((tag) => (
              <Badge key={tag.id} variant="secondary" className="text-xs">
                {tag.name}
              </Badge>
            ))}
          </div>
        )}
      </CardContent>

      {(onApply || onToggleFavorite) && (
        <CardFooter className="flex gap-2">
          {onApply && isAuthenticated && (
            <Button onClick={() => onApply(opportunity.id)} className="flex-1">
              Откликнуться
            </Button>
          )}
          {onApply && !isAuthenticated && (
            <Button variant="outline" className="flex-1" disabled>
              Войдите для отклика
            </Button>
          )}
        </CardFooter>
      )}
    </Card>
  );
};
