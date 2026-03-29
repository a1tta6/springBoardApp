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

  async getCompanyProfile(id: string) {
    const data = await request<{ company: Company; opportunities: ApiOpportunity[] }>(`/v1/companies/${id}`);
    return {
      ...data,
      opportunities: data.opportunities.map(opportunity),
    };
  },

  async getOpportunities() {
    return (await request<ApiOpportunity[]>('/v1/opportunities')).map(opportunity);
  },

  async getOpportunity(id: string) {
    return opportunity(await request<ApiOpportunity>(`/v1/opportunities/${id}`));
  },

  async getHasApplied(opportunityId: string): Promise<boolean> {
    return request<boolean>(`/v1/applicant/opportunities/${opportunityId}/applied`);
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

  cancelApplication(opportunityId: string) {
    return request<void>(`/v1/applicant/opportunities/${opportunityId}/applications`, {
      method: 'DELETE',
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

  getEmployerApplications() {
    return request<ApiApplication[]>('/v1/employer/applications').then((apps) => apps.map(application));
  },

  getEmployerApplicants() {
    return request<User[]>('/v1/employer/applicants');
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
    eventDate?: string;
    expiryDate?: string;
  }) {
    return request<ApiOpportunity>('/v1/employer/opportunities', {
      method: 'POST',
      body: JSON.stringify(data),
    }).then(opportunity);
  },

  updateEmployerOpportunity(id: string, data: {
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
    eventDate?: string;
    expiryDate?: string;
  }) {
    return request<ApiOpportunity>(`/v1/employer/opportunities/${id}`, {
      method: 'PATCH',
      body: JSON.stringify(data),
    }).then(opportunity);
  },

  deleteOpportunity(id: string) {
    return request<void>(`/v1/employer/opportunities/${id}`, {
      method: 'DELETE',
    });
  },

  getVerificationStatus() {
    return request<VerificationRequest>('/v1/employer/verification');
  },

  submitVerification() {
    return request<VerificationRequest>('/v1/employer/verification/submit', {
      method: 'POST',
    });
  },

  updateCompanyProfile(data: {
    name?: string;
    inn?: string;
    ogrn?: string;
    address?: string;
    website?: string;
    logo?: string;
    socialLinks?: string;
    bio?: string;
  }) {
    return request<void>('/v1/employer/company', {
      method: 'PATCH',
      body: JSON.stringify(data),
    });
  },

  updateEmployerBio(bio: string) {
    return request<void>('/v1/employer/bio', {
      method: 'PATCH',
      body: JSON.stringify({ bio }),
    });
  },

  getCuratorPendingVerifications() {
    return request<VerificationRequest[]>('/v1/curator/verifications/pending');
  },

  approveVerification(requestId: string) {
    return request<void>(`/v1/curator/verifications/${requestId}/approve`, {
      method: 'POST',
    });
  },

  rejectVerification(requestId: string, reason: string) {
    return request<void>(`/v1/curator/verifications/${requestId}/reject`, {
      method: 'POST',
      body: JSON.stringify({ reason }),
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

  getFriends() {
    return request<Friend[]>('/v1/applicant/friends');
  },

  getPendingFriendRequests() {
    return request<Friend[]>('/v1/applicant/friends/requests');
  },

  getSentFriendRequests() {
    return request<Friend[]>('/v1/applicant/friends/sent');
  },

  sendFriendRequest(userId: string) {
    return request<void>(`/v1/applicant/friends/${userId}`, {
      method: 'POST',
    });
  },

  acceptFriendRequest(userId: string) {
    return request<void>(`/v1/applicant/friends/${userId}/accept`, {
      method: 'POST',
    });
  },

  rejectFriendRequest(userId: string) {
    return request<void>(`/v1/applicant/friends/${userId}/reject`, {
      method: 'POST',
    });
  },

  removeFriend(userId: string) {
    return request<void>(`/v1/applicant/friends/${userId}`, {
      method: 'DELETE',
    });
  },

  cancelFriendRequest(userId: string) {
    return request<void>(`/v1/applicant/friends/${userId}/cancel`, {
      method: 'DELETE',
    });
  },

  getFriendStatus(userId: string) {
    return request<FriendStatus>(`/v1/applicant/friends/${userId}/status`);
  },

  searchUsers(query: string) {
    return request<User[]>(`/v1/applicant/users/search?q=${encodeURIComponent(query)}`);
  },

  getUserProfile(userId: string) {
    return request<UserProfile>(`/v1/applicant/users/${userId}`);
  },
};

export type { User } from '../types';

export interface VerificationRequest {
  id: string;
  companyId: string;
  status: 'pending' | 'approved' | 'rejected';
  rejectionReason?: string;
  createdAt: string;
  processedAt?: string;
}

export interface FriendStatus {
  status: 'none' | 'friends' | 'sent' | 'pending';
  friendId?: string;
}

export interface Friend {
  id: string;
  userId: string;
  email: string;
  displayName: string;
  fullName?: string;
  university?: string;
  status: string;
  createdAt: string;
}

export interface UserProfile {
  user: User;
  isFriend: boolean;
  showResume: boolean;
  showApplications: boolean;
  favorites: Opportunity[];
  applications: Application[];
}
