import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';

// Components
import { LayoutComponent } from './core/component/layout/layout.component';
import { DashboardComponent } from './core/component/dashboard/dashboard.component';
import { UserIndexComponent } from './features/platform/component/user/index.component';
import { AccountIndexComponent } from './features/platform/component/account/index.component';
import { AccountEditComponent } from './features/platform/component/account/edit.component';
import { ContactIndexComponent } from './features/platform/component/contact/index.component';
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
      { path: 'users', component: UserIndexComponent },
      { path: 'accounts', component: AccountIndexComponent },
      { path: 'accounts/edit/:mode', component: AccountEditComponent },
      { path: 'contacts', component: ContactIndexComponent }
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
