import React, { useState } from 'react';
import { useNavigate } from 'react-router';
import { useAuth } from '../context/AuthContext';
import { UserRole } from '../types';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '../components/ui/card';
import { RadioGroup, RadioGroupItem } from '../components/ui/radio-group';
import { toast } from 'sonner';
import { UserPlus, ArrowLeft, User, Building } from 'lucide-react';

export const RegisterPage: React.FC = () => {
  const navigate = useNavigate();
  const { register } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [displayName, setDisplayName] = useState('');
  const [role, setRole] = useState<UserRole>('applicant');
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    // Демонстрационная задержка
    await new Promise((resolve) => setTimeout(resolve, 500));

    const success = register(email, password, displayName, role);

    if (success) {
      toast.success('Регистрация успешна! Добро пожаловать!');
      
      if (role === 'employer') {
        toast.info('Для публикации вакансий требуется верификация компании');
      }

      // Перенаправление в зависимости от роли
      if (role === 'applicant') {
        navigate('/dashboard/applicant');
      } else if (role === 'employer') {
        navigate('/dashboard/employer');
      }
    } else {
      toast.error('Пользователь с таким email уже существует');
    }

    setIsLoading(false);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-50 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <Button
          variant="ghost"
          onClick={() => navigate('/')}
          className="mb-4"
        >
          <ArrowLeft className="w-4 h-4 mr-2" />
          На главную
        </Button>

        <Card>
          <CardHeader className="text-center">
            <div className="w-16 h-16 bg-gradient-to-br from-blue-500 to-purple-600 rounded-lg flex items-center justify-center mx-auto mb-4">
              <span className="text-white font-bold text-2xl">Т</span>
            </div>
            <CardTitle className="text-2xl">Регистрация</CardTitle>
            <CardDescription>Создайте новый аккаунт для доступа к платформе</CardDescription>
          </CardHeader>

          <form onSubmit={handleSubmit}>
            <CardContent className="space-y-4">
              <div className="space-y-3">
                <Label>Выберите роль</Label>
                <RadioGroup value={role} onValueChange={(value) => setRole(value as UserRole)}>
                  <div className="flex items-center space-x-2 border rounded-lg p-3 cursor-pointer hover:bg-gray-50">
                    <RadioGroupItem value="applicant" id="applicant" />
                    <Label htmlFor="applicant" className="flex items-center gap-2 cursor-pointer flex-1">
                      <User className="w-4 h-4" />
                      <div>
                        <p className="font-medium">Соискатель</p>
                        <p className="text-xs text-gray-500">Студент или выпускник</p>
                      </div>
                    </Label>
                  </div>

                  <div className="flex items-center space-x-2 border rounded-lg p-3 cursor-pointer hover:bg-gray-50">
                    <RadioGroupItem value="employer" id="employer" />
                    <Label htmlFor="employer" className="flex items-center gap-2 cursor-pointer flex-1">
                      <Building className="w-4 h-4" />
                      <div>
                        <p className="font-medium">Работодатель</p>
                        <p className="text-xs text-gray-500">Компания или ИП</p>
                      </div>
                    </Label>
                  </div>
                </RadioGroup>
              </div>

              <div className="space-y-2">
                <Label htmlFor="displayName">Отображаемое имя</Label>
                <Input
                  id="displayName"
                  placeholder={role === 'applicant' ? 'Иван Петров' : 'Название компании'}
                  value={displayName}
                  onChange={(e) => setDisplayName(e.target.value)}
                  required
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="email">Email</Label>
                <Input
                  id="email"
                  type="email"
                  placeholder={role === 'employer' ? 'hr@company.ru (корпоративный)' : 'example@mail.ru'}
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
                {role === 'employer' && (
                  <p className="text-xs text-gray-500">
                    Для работодателей рекомендуется корпоративная почта для верификации
                  </p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="password">Пароль</Label>
                <Input
                  id="password"
                  type="password"
                  placeholder="Минимум 6 символов"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  minLength={6}
                />
              </div>

              {role === 'employer' && (
                <div className="bg-amber-50 border border-amber-200 rounded-lg p-3">
                  <p className="text-sm text-amber-800">
                    <strong>Внимание:</strong> После регистрации потребуется верификация компании куратором платформы
                    для получения доступа к публикации вакансий и мероприятий.
                  </p>
                </div>
              )}
            </CardContent>

            <CardFooter className="flex flex-col gap-3">
              <Button type="submit" className="w-full" disabled={isLoading}>
                <UserPlus className="w-4 h-4 mr-2" />
                {isLoading ? 'Регистрация...' : 'Зарегистрироваться'}
              </Button>

              <p className="text-sm text-center text-gray-600">
                Уже есть аккаунт?{' '}
                <button
                  type="button"
                  onClick={() => navigate('/login')}
                  className="text-blue-600 hover:underline"
                >
                  Войти
                </button>
              </p>
            </CardFooter>
          </form>
        </Card>
      </div>
    </div>
  );
};
