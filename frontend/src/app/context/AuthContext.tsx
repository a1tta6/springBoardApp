import React, { createContext, useContext, useState, ReactNode } from 'react';
import { User, UserRole } from '../types';
import { users } from '../data/mockData';

interface AuthContextType {
  currentUser: User | null;
  login: (email: string, password: string) => boolean;
  logout: () => void;
  register: (email: string, password: string, displayName: string, role: UserRole) => boolean;
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

  const login = (email: string, password: string): boolean => {
    // Простая проверка для демонстрации
    const user = users.find(u => u.email === email);
    if (user) {
      setCurrentUser(user);
      localStorage.setItem('currentUser', JSON.stringify(user));
      return true;
    }
    return false;
  };

  const logout = () => {
    setCurrentUser(null);
    localStorage.removeItem('currentUser');
  };

  const register = (email: string, password: string, displayName: string, role: UserRole): boolean => {
    // Проверка, что пользователь не существует
    const existingUser = users.find(u => u.email === email);
    if (existingUser) {
      return false;
    }

    const newUser: User = {
      id: `u${users.length + 1}`,
      email,
      displayName,
      role,
    };

    users.push(newUser);
    setCurrentUser(newUser);
    localStorage.setItem('currentUser', JSON.stringify(newUser));
    return true;
  };

  // Проверка сохраненного пользователя при загрузке
  React.useEffect(() => {
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
      setCurrentUser(JSON.parse(savedUser));
    }
  }, []);

  return (
    <AuthContext.Provider value={{ currentUser, login, logout, register }}>
      {children}
    </AuthContext.Provider>
  );
};
