import { createBrowserRouter } from 'react-router';
import { HomePage } from './pages/HomePage';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { ApplicantDashboard } from './pages/ApplicantDashboard';
import { EmployerDashboard } from './pages/EmployerDashboard';
import { CuratorDashboard } from './pages/CuratorDashboard';
import { NotFoundPage } from './pages/NotFoundPage';

export const router = createBrowserRouter([
  {
    path: '/',
    Component: HomePage,
  },
  {
    path: '/login',
    Component: LoginPage,
  },
  {
    path: '/register',
    Component: RegisterPage,
  },
  {
    path: '/dashboard/applicant',
    Component: ApplicantDashboard,
  },
  {
    path: '/dashboard/employer',
    Component: EmployerDashboard,
  },
  {
    path: '/dashboard/curator',
    Component: CuratorDashboard,
  },
  {
    path: '*',
    Component: NotFoundPage,
  },
]);