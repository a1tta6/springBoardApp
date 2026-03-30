import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router';
import { Company, Opportunity, Tag } from '../types';
import { Card, CardHeader, CardTitle, CardDescription, CardContent, CardFooter } from './ui/card';
import { Badge } from './ui/badge';
import { Button } from './ui/button';
import { MapPin, Building2, Calendar, Banknote, Briefcase, Star, ArrowRight, XCircle } from 'lucide-react';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from './ui/dialog';
import { Textarea } from './ui/textarea';

interface OpportunityCardProps {
  opportunity: Opportunity;
  companies: Company[];
  tags: Tag[];
  isFavorite?: boolean;
  hasApplied?: boolean;
  onToggleFavorite?: (id: string) => void;
  onApply?: (id: string) => void;
  onApplyWithMessage?: (id: string, message: string) => void;
  isAuthenticated?: boolean;
  onViewDetails?: (id: string) => void;
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
  hasApplied = false,
  onToggleFavorite,
  onApply,
  onApplyWithMessage,
  isAuthenticated = false,
  onViewDetails,
}) => {
  const navigate = useNavigate();
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [coverLetter, setCoverLetter] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const company = companies.find((item) => item.id === opportunity.companyId);
  const opportunityTags = tags.filter((tag) => opportunity.tags.includes(tag.id));

  const requiresCoverLetter = opportunity.type === 'internship' || opportunity.type === 'vacancy';

  const handleViewDetails = () => {
    if (onViewDetails) {
      onViewDetails(opportunity.id);
    } else {
      navigate(`/opportunity/${opportunity.id}`);
    }
  };

  const handleApplyClick = () => {
    if (requiresCoverLetter && onApplyWithMessage) {
      setIsDialogOpen(true);
    } else if (onApply) {
      onApply(opportunity.id);
    }
  };

  const handleSubmitApplication = async () => {
    if (onApplyWithMessage) {
      setIsSubmitting(true);
      try {
        await onApplyWithMessage(opportunity.id, coverLetter);
        setIsDialogOpen(false);
        setCoverLetter('');
      } finally {
        setIsSubmitting(false);
      }
    }
  };

  const handleCancelDialog = () => {
    setIsDialogOpen(false);
    setCoverLetter('');
  };

  return (
    <Card className="hover:shadow-lg transition-shadow">
      <CardHeader>
        <div className="flex justify-between items-start">
          <div className="flex-1">
            <CardTitle className="mb-1">{opportunity.title}</CardTitle>
            <CardDescription className="flex items-center gap-2">
              <Building2 className="w-4 h-4" />
              <Link to={`/company/${company?.id}`} className="hover:text-blue-600">
                {company?.name}
              </Link>
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
            <span>
              {opportunity.workFormat === 'remote' 
                ? opportunity.location.city 
                : opportunity.location.address || opportunity.location.city}
            </span>
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

      {(onApply || onToggleFavorite || onViewDetails) && (
        <CardFooter className="flex gap-2">
          <Button variant="outline" onClick={handleViewDetails} className="flex-1">
            Подробнее
            <ArrowRight className="w-4 h-4 ml-2" />
          </Button>
          {onApply && isAuthenticated && hasApplied && (
            <Button variant="outline" onClick={() => onApply(opportunity.id)}>
              <XCircle className="w-4 h-4 mr-2" />
              Отменить отклик
            </Button>
          )}
          {(onApply || onApplyWithMessage) && isAuthenticated && !hasApplied && (
            <Button onClick={handleApplyClick}>
              Откликнуться
            </Button>
          )}
          {(onApply || onApplyWithMessage) && !isAuthenticated && (
            <Button variant="outline" disabled>
              Войдите для отклика
            </Button>
          )}
        </CardFooter>
      )}

      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Сопроводительное письмо</DialogTitle>
            <DialogDescription>
              Расскажите о себе и почему вас заинтересовала эта позиция
            </DialogDescription>
          </DialogHeader>
          <Textarea
            value={coverLetter}
            onChange={(e) => setCoverLetter(e.target.value)}
            placeholder="Опишите ваш опыт и мотивацию..."
            className="min-h-[150px]"
          />
          <DialogFooter>
            <Button variant="outline" onClick={handleCancelDialog}>
              Отмена
            </Button>
            <Button onClick={handleSubmitApplication} disabled={isSubmitting}>
              {isSubmitting ? 'Отправка...' : 'Отправить'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </Card>
  );
};
