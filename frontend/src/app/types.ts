export type UserRole = 'applicant' | 'employer' | 'curator';

export type OpportunityType = 'internship' | 'vacancy' | 'mentorship' | 'event';

export type WorkFormat = 'office' | 'hybrid' | 'remote';

export type EmploymentType = 'full-time' | 'part-time' | 'project';

export type ExperienceLevel = 'intern' | 'junior' | 'middle' | 'senior';

export type ApplicationStatus = 'pending' | 'accepted' | 'rejected' | 'reserved';

export interface Tag {
  id: string;
  name: string;
  category: 'technology' | 'level' | 'employment';
}

export interface Company {
  id: string;
  name: string;
  inn?: string;
  ogrn?: string;
  address?: string;
  website?: string;
  logo?: string;
  socialLinks?: string;
  bio?: string;
  verified: boolean;
  email: string;
}

export interface User {
  id: string;
  email: string;
  username?: string;
  displayName: string;
  role: UserRole;
  // Для соискателя
  fullName?: string;
  university?: string;
  course?: string;
  graduationYear?: string;
  skills?: string[];
  portfolioLinks?: string[];
  resume?: string;
  contacts?: string[];
  privacySettings?: {
    showApplications: boolean;
    showResume: boolean;
  };
  // Для работодателя
  companyId?: string;
  photo?: string;
}

export interface Opportunity {
  id: string;
  title: string;
  description: string;
  type: OpportunityType;
  companyId: string;
  workFormat: WorkFormat;
  location: {
    city: string;
    address?: string;
    coordinates: [number, number];
  };
  salary?: {
    min?: number;
    max?: number;
    currency: string;
  };
  publishedDate: Date;
  expiryDate?: Date;
  eventDate?: Date;
  contactInfo: {
    email?: string;
    phone?: string;
    website?: string;
  };
  tags: string[];
  status: 'active' | 'closed' | 'planned';
  requirements?: string;
  mediaContent?: string[];
}

export interface Application {
  id: string;
  opportunityId: string;
  applicantId: string;
  status: ApplicationStatus;
  appliedDate: Date;
  message?: string;

}
export const statusMap: Record<ApplicationStatus, string> = {
  accepted: 'Принят',
  rejected: 'Отклонен',
  pending: 'В обработке',
  reserved: 'В резерве'
};

export interface CompanyProfile {
  company: Company;
  opportunities: Opportunity[];
}

export interface Recommendation {
  id: string;
  referrer: User;
  referee: User;
  subjectUser?: User;
  opportunity?: Opportunity;
  comment?: string;
  createdAt: string;
}

