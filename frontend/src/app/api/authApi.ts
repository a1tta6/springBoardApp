import { User, UserRole } from '../types';
import { request } from './http';

interface RegisterPayload {
  email: string;
  password: string;
  displayName: string;
  role: UserRole;
}

/**
 * Authentication client for cookie based backend auth.
 *
 * Example:
 * The auth context logs in, registers and restores the current user through this module.
 */
export const authApi = {
  async login(email: string, password: string): Promise<User> {
    await request<void>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({
        username: email,
        password,
      }),
    });
    return this.me();
  },

  register(payload: RegisterPayload) {
    return request<User>('/auth/register', {
      method: 'POST',
      body: JSON.stringify({
        username: payload.email,
        email: payload.email,
        password: payload.password,
        displayName: payload.displayName,
        role: payload.role,
      }),
    });
  },

  me() {
    return request<User>('/auth/me');
  },

  refresh() {
    return request<void>('/auth/refresh', {
      method: 'POST',
    });
  },

  logout() {
    return request<void>('/auth/logout', {
      method: 'POST',
    });
  },
};
