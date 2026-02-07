import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';

// Components
import { LayoutComponent } from './core/component/layout/layout.component';
import { DashboardComponent } from './core/component/dashboard/dashboard.component';
import { UsersComponent } from './features/component/platform/users/users.component';
import { AccountsComponent } from './features/component/platform/accounts/accounts.component';
import { ContactsComponent } from './features/component/platform/contacts/contacts.component';
import { LoginComponent } from './core/component/login/login.component';
import { OauthCallbackComponent } from './core/component/oauth-callback/oauth-callback.component';

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
