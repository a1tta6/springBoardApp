import React, { useState } from 'react';
import { useNavigate } from 'react-router';
import { useAuth } from '../context/AuthContext';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '../components/ui/card';
import { toast } from 'sonner';
import { LogIn, ArrowLeft } from 'lucide-react';

export const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      const user = await login(email, password);
      toast.success('Вход выполнен успешно!');
      if (user?.role === 'applicant') {
        navigate('/dashboard/applicant');
      } else if (user?.role === 'employer') {
        navigate('/dashboard/employer');
      } else if (user?.role === 'curator') {
        navigate('/dashboard/curator');
      } else {
        navigate('/');
      }
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Неверный email или пароль');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-50 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <Button variant="ghost" onClick={() => navigate('/')} className="mb-4">
          <ArrowLeft className="w-4 h-4 mr-2" />
          На главную
        </Button>

        <Card>
          <CardHeader className="text-center">
            <div className="w-16 h-16 bg-gradient-to-br from-blue-500 to-purple-600 rounded-lg flex items-center justify-center mx-auto mb-4" onClick={() => navigate('/')}>
              <span className="text-white font-bold text-2xl">Т</span>
            </div>
            <CardTitle className="text-2xl">Вход в систему</CardTitle>
            <CardDescription>Введите ваши данные для входа</CardDescription>
          </CardHeader>

          <form onSubmit={handleSubmit}>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="email">Email</Label>
                <Input id="email" type="email" placeholder="example@mail.ru" value={email} onChange={(e) => setEmail(e.target.value)} required />
              </div>

              <div className="space-y-2">
                <Label htmlFor="password">Пароль</Label>
                <Input id="password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
              </div>
            </CardContent>

            <CardFooter className="flex flex-col gap-3 pt-6">
              <Button type="submit" className="w-full" disabled={isLoading}>
                <LogIn className="w-4 h-4 mr-2" />
                {isLoading ? 'Вход...' : 'Войти'}
              </Button>

              <p className="text-sm text-center text-gray-600">
                Нет аккаунта?{' '}
                <button type="button" onClick={() => navigate('/register')} className="text-blue-600 hover:underline">
                  Зарегистрироваться
                </button>
              </p>
            </CardFooter>
          </form>
        </Card>
      </div>
    </div>
  );
};
