import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './features/login/login.component';
import { OauthCallbackComponent } from './features/oauth-callback/oauth-callback.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { UsersComponent } from './features/users/users.component';
import { AccountsComponent } from './features/accounts/accounts.component';
import { ContactsComponent } from './features/contacts/contacts.component';
import { AuthGuard } from './core/guards/auth.guard';

const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'oauth/callback', component: OauthCallbackComponent },
  { 
    path: 'dashboard', 
    component: DashboardComponent,
    canActivate: [AuthGuard]
  },
  { 
    path: 'users', 
    component: UsersComponent,
    canActivate: [AuthGuard]
  },
  { 
    path: 'accounts', 
    component: AccountsComponent,
    canActivate: [AuthGuard]
  },
  { 
    path: 'contacts', 
    component: ContactsComponent,
    canActivate: [AuthGuard]
  },
  { path: '**', redirectTo: '/dashboard' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

