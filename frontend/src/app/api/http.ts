const API_URL = 'http://localhost:9500/api';

/**
 * Shared http helper for backend requests.
 *
 * Example:
 * API modules use this function to send authenticated requests with cookies.
 */
export async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${API_URL}${path}`, {
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      ...(init?.headers ?? {}),
    },
    ...init,
  });

  if (!res.ok) {
    let message = `Request failed with status ${res.status}`;
    try {
      const error = await res.json();
      if (typeof error?.message === 'string') {
        message = error.message;
      }
    } catch {
      //
    }
    throw new Error(message);
  }

  if (res.status === 204) {
    return undefined as T;
  }

  const text = await res.text();
  return (text ? JSON.parse(text) : undefined) as T;
}
