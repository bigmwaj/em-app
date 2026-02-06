import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';

// Components
import { LayoutComponent } from './shared/components/layout/layout.component';
import { LoginComponent } from './features/login/login.component';
import { OauthCallbackComponent } from './features/oauth-callback/oauth-callback.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { UsersComponent } from './features/users/users.component';
import { AccountsComponent } from './features/accounts/accounts.component';
import { ContactsComponent } from './features/contacts/contacts.component';

const routes: Routes = [
  // Public routes
  { path: 'login', component: LoginComponent },
  { path: 'oauth/callback', component: OauthCallbackComponent },
  
  // Protected routes with layout
  {
    path: '',
    component: LayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'users', component: UsersComponent },
      { path: 'accounts', component: AccountsComponent },
      { path: 'contacts', component: ContactsComponent }
    ]
  },
  
  // Redirect any unknown routes to login
  { path: '**', redirectTo: 'login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
