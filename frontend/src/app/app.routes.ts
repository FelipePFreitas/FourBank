import { Routes } from '@angular/router';
import { authGuard } from './core/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.component').then((m) => m.LoginComponent),
    title: 'Entrar | FourBank',
  },
  {
    path: 'cadastro',
    loadComponent: () => import('./pages/cadastro/cadastro.component').then((m) => m.CadastroComponent),
    title: 'Cadastro | FourBank',
  },
  {
    path: '',
    pathMatch: 'full',
    loadComponent: () => import('./pages/home/home.component').then((m) => m.HomeComponent),
    title: 'FourBank | Banco digital',
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./layout/shell.component').then((m) => m.ShellComponent),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      {
        path: 'dashboard',
        loadComponent: () => import('./pages/dashboard/dashboard.component').then((m) => m.DashboardComponent),
        title: 'Dashboard | FourBank',
      },
      {
        path: 'operacoes',
        loadComponent: () => import('./pages/operacoes/operacoes.component').then((m) => m.OperacoesComponent),
        title: 'Operações | FourBank',
      },
      {
        path: 'historico',
        loadComponent: () => import('./pages/historico/historico.component').then((m) => m.HistoricoComponent),
        title: 'Histórico | FourBank',
      },
    ],
  },
  { path: '**', redirectTo: '' },
];
