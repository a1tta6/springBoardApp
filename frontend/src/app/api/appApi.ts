import { Application, Company, Opportunity, Tag, User } from '../types';
import { request } from './http';

type ApiOpportunity = Omit<Opportunity, 'publishedDate' | 'expiryDate' | 'eventDate'> & {
  publishedDate: string;
  expiryDate?: string;
  eventDate?: string;
};

type ApiApplication = Omit<Application, 'appliedDate'> & {
  appliedDate: string;
};

function opportunity(item: ApiOpportunity): Opportunity {
  return {
    ...item,
    publishedDate: new Date(item.publishedDate),
    expiryDate: item.expiryDate ? new Date(item.expiryDate) : undefined,
    eventDate: item.eventDate ? new Date(item.eventDate) : undefined,
  };
}

function application(item: ApiApplication): Application {
  return {
    ...item,
    appliedDate: new Date(item.appliedDate),
  };
}

export const appApi = {
  getTags() {
    return request<Tag[]>('/v1/tags');
  },

  getCompanies() {
    return request<Company[]>('/v1/companies');
  },

  async getOpportunities() {
    return (await request<ApiOpportunity[]>('/v1/opportunities')).map(opportunity);
  },

  updateApplicantProfile(data: {
    displayName: string;
    fullName: string;
    university: string;
    course: string;
    graduationYear: string;
    skills: string[];
    portfolioLinks: string[];
    resume: string;
    contacts: string[];
  }) {
    return request<User>('/v1/applicant/profile', {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  },

  updateApplicantPrivacy(data: { showApplications: boolean; showResume: boolean }) {
    return request<User>('/v1/applicant/privacy', {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  },

  async getApplicantApplications() {
    return (await request<ApiApplication[]>('/v1/applicant/applications')).map(application);
  },

  applyToOpportunity(opportunityId: string, message?: string) {
    return request<void>(`/v1/applicant/opportunities/${opportunityId}/applications`, {
      method: 'POST',
      body: JSON.stringify({ message }),
    });
  },

  async getFavorites() {
    return (await request<ApiOpportunity[]>('/v1/applicant/favorites')).map(opportunity);
  },

  addFavorite(opportunityId: string) {
    return request<void>(`/v1/applicant/favorites/${opportunityId}`, {
      method: 'POST',
    });
  },

  removeFavorite(opportunityId: string) {
    return request<void>(`/v1/applicant/favorites/${opportunityId}`, {
      method: 'DELETE',
    });
  },

  async getEmployerOpportunities() {
    return (await request<ApiOpportunity[]>('/v1/employer/opportunities')).map(opportunity);
  },

  createEmployerOpportunity(data: {
    title: string;
    description: string;
    type: string;
    workFormat: string;
    city: string;
    address?: string;
    latitude: number;
    longitude: number;
    salaryMin?: number;
    salaryMax?: number;
    currency?: string;
    contactEmail?: string;
    contactPhone?: string;
    contactWebsite?: string;
    tags?: string[];
    requirements?: string;
  }) {
    return request<ApiOpportunity>('/v1/employer/opportunities', {
      method: 'POST',
      body: JSON.stringify(data),
    }).then(opportunity);
  },

  async getEmployerApplications() {
    return (await request<ApiApplication[]>('/v1/employer/applications')).map(application);
  },

  getEmployerApplicants() {
    return request<User[]>('/v1/employer/applicants');
  },

  updateEmployerApplicationStatus(applicationId: string, status: string) {
    return request<void>(`/v1/employer/applications/${applicationId}`, {
      method: 'PATCH',
      body: JSON.stringify({ status }),
    });
  },

  getCuratorPendingCompanies() {
    return request<Company[]>('/v1/curator/companies/pending');
  },

  verifyCompany(companyId: string) {
    return request<void>(`/v1/curator/companies/${companyId}/verify`, {
      method: 'PATCH',
    });
  },

  async getCuratorPendingOpportunities() {
    return (await request<ApiOpportunity[]>('/v1/curator/opportunities/pending')).map(opportunity);
  },

  moderateOpportunity(opportunityId: string, status: string) {
    return request<void>(`/v1/curator/opportunities/${opportunityId}`, {
      method: 'PATCH',
      body: JSON.stringify({ status }),
    });
  },

  getCuratorUsers() {
    return request<User[]>('/v1/curator/users');
  },

  blockUser(userId: string) {
    return request<void>(`/v1/curator/users/${userId}/block`, {
      method: 'PATCH',
    });
  },
};
