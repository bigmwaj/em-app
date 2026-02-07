import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
//import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

// Angular Material Modules
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialogModule } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTableModule } from '@angular/material/table';
import { ReactiveFormsModule } from '@angular/forms';

import { App } from './app';
import { AppRoutingModule } from './app-routing-module';

// Core Components
import { LayoutComponent } from './core/component/layout/layout.component';
import { LoginComponent } from './core/component/login/login.component';
import { OauthCallbackComponent } from './core/component/oauth-callback/oauth-callback.component';
import { DashboardComponent } from './core/component/dashboard/dashboard.component';

// Feature Components
import { AccountsComponent } from './features/component/platform/accounts/accounts.component';
import { ContactsComponent } from './features/component/platform/contacts/contacts.component';
import { UsersComponent } from './features/component/platform/users/users.component';

// Dialog Components
import { UserDialogComponent } from './features/component/platform/users/user-dialog/user-dialog.component';
import { AccountDialogComponent } from './features/component/platform/accounts/account-dialog/account-dialog.component';
import { ContactDialogComponent } from './features/component/platform/contacts/contact-dialog/contact-dialog.component';
import { ConfirmDialogComponent } from './shared/components/confirm-dialog/confirm-dialog.component';

// Interceptors
import { ErrorInterceptor } from './core/interceptors/error.interceptor';
import { JwtInterceptor } from './core/interceptors/jwt.interceptor';

@NgModule({
  declarations: [
    App,
    LayoutComponent,
    LoginComponent,
    OauthCallbackComponent,
    DashboardComponent,
    UsersComponent,
    AccountsComponent,
    ContactsComponent,
    UserDialogComponent,
    AccountDialogComponent,
    ContactDialogComponent,
    ConfirmDialogComponent
  ],
  imports: [
    BrowserModule,
    //BrowserAnimationsModule,
    AppRoutingModule,
    ReactiveFormsModule,
    // Material Modules
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatSidenavModule,
    MatListModule,
    MatCardModule,
    MatMenuModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatDividerModule,
    MatTableModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSnackBarModule
  ],
  providers: [
    provideHttpClient(withInterceptorsFromDi()),
    provideBrowserGlobalErrorListeners(),
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true }
  ],
  bootstrap: [App]
})
export class AppModule { }