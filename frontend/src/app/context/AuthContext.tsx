import React, { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import { User, UserRole } from '../types';
import { authApi } from '../api/authApi';

interface AuthContextType {
  currentUser: User | null;
  isReady: boolean;
  login: (email: string, password: string) => Promise<User | null>;
  logout: () => Promise<void>;
  register: (email: string, password: string, displayName: string, role: UserRole) => Promise<User | null>;
  refreshUser: () => Promise<User | null>;
  syncUser: (user: User | null) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const [isReady, setIsReady] = useState(false);

  const syncUser = (user: User | null) => {
    setCurrentUser(user);
  };

  const refreshUser = async (): Promise<User | null> => {
    try {
      const user = await authApi.me();
      setCurrentUser(user);
      return user;
    } catch {
      try {
        await authApi.refresh();
        const user = await authApi.me();
        setCurrentUser(user);
        return user;
      } catch {
        setCurrentUser(null);
        return null;
      }
    }
  };

  const login = async (email: string, password: string): Promise<User | null> => {
    const user = await authApi.login(email, password);
    setCurrentUser(user);
    return user;
  };

  const logout = async (): Promise<void> => {
    try {
      await authApi.logout();
    } finally {
      setCurrentUser(null);
    }
  };

  const register = async (
    email: string,
    password: string,
    displayName: string,
    role: UserRole
  ): Promise<User | null> => {
    const user = await authApi.register({ email, password, displayName, role });
    setCurrentUser(user);
    return user;
  };

  useEffect(() => {
    void refreshUser().finally(() => {
      setIsReady(true);
    });
  }, []);

  return (
    <AuthContext.Provider value={{ currentUser, isReady, login, logout, register, refreshUser, syncUser }}>
      {children}
    </AuthContext.Provider>
  );
};
