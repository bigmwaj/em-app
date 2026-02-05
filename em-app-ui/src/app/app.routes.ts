import { Routes } from '@angular/router';
import { LoginComponent } from './features/login/login.component';
import { OauthCallbackComponent } from './features/oauth-callback/oauth-callback.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { UsersComponent } from './features/users/users.component';
import { AccountsComponent } from './features/accounts/accounts.component';
import { ContactsComponent } from './features/contacts/contacts.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'oauth/callback', component: OauthCallbackComponent },
  { 
    path: 'dashboard', 
    component: DashboardComponent,
    canActivate: [authGuard]
  },
  { 
    path: 'users', 
    component: UsersComponent,
    canActivate: [authGuard]
  },
  { 
    path: 'accounts', 
    component: AccountsComponent,
    canActivate: [authGuard]
  },
  { 
    path: 'contacts', 
    component: ContactsComponent,
    canActivate: [authGuard]
  },
  { path: '**', redirectTo: '/dashboard' }
];
