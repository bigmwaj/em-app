import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { provideNativeDateAdapter } from '@angular/material/core';

// Angular Material Modules
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDialogModule } from '@angular/material/dialog';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatExpansionModule } from '@angular/material/expansion';

import { App } from './app';
import { AppRoutingModule } from './app-routing-module';

// Core Components
import { LayoutComponent } from './core/component/layout/layout.component';
import { LoginComponent } from './core/component/login/login.component';
import { OauthCallbackComponent } from './core/component/oauth-callback/oauth-callback.component';
import { DashboardComponent } from './core/component/dashboard/dashboard.component';

// Feature Components
import { AccountIndexComponent } from './features/platform/component/account/index.component';
import { AccountEditComponent } from './features/platform/component/account/edit.component';
import { AccountChangeStatusDialogComponent } from './features/platform/component/account/change-status-dialog.component';
import { AccountDeleteDialogComponent } from './features/platform/component/account/delete-dialog.component';
import { EditAccountDetailsComponent } from './features/platform/component/account/edit/account-details.component';
import { EditPrincipalAccountContactComponent } from './features/platform/component/account/edit/principal-account-contact.component';
import { EditAccountAdminUserFormComponent } from './features/platform/component/account/edit/account-admin-user.component';
import { ContactIndexComponent } from './features/platform/component/contact/index.component';
import { ContactEditComponent } from './features/platform/component/contact/edit.component';
import { ContactDeleteDialogComponent } from './features/platform/component/contact/delete-dialog.component';
import { UserIndexComponent } from './features/platform/component/user/index.component';
import { UserEditComponent } from './features/platform/component/user/edit.component';
import { UserChangeStatusDialogComponent } from './features/platform/component/user/change-status-dialog.component';
import { UserDeleteDialogComponent } from './features/platform/component/user/delete-dialog.component';


// Interceptors
import { ErrorInterceptor } from './core/interceptors/error.interceptor';
import { JwtInterceptor } from './core/interceptors/jwt.interceptor';
import { ChangeStatusDialogComponent } from './features/shared/component/change-status-dialog.component';
import { DeleteDialogComponent } from './features/shared/component/delete-dialog.component';

@NgModule({
  declarations: [
    App,
    LayoutComponent,
    LoginComponent,
    OauthCallbackComponent,
    DashboardComponent,
    UserIndexComponent,
    UserEditComponent,
    UserChangeStatusDialogComponent,
    UserDeleteDialogComponent,
    AccountIndexComponent,
    AccountEditComponent,
    EditAccountDetailsComponent,
    EditPrincipalAccountContactComponent,
    EditAccountAdminUserFormComponent,
    AccountChangeStatusDialogComponent,
    AccountDeleteDialogComponent,
    ContactIndexComponent,
    ContactEditComponent,
    ContactDeleteDialogComponent,
    ChangeStatusDialogComponent,
    DeleteDialogComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    FormsModule,
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
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDialogModule,
    MatTableModule,
    MatPaginatorModule,
    MatDatepickerModule,
    MatExpansionModule,
  ],
  providers: [
    provideHttpClient(withInterceptorsFromDi()),
    provideBrowserGlobalErrorListeners(),
    provideNativeDateAdapter(),
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true }
  ],
  bootstrap: [App]
})
export class AppModule { }
