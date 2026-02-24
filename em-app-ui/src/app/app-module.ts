import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideNativeDateAdapter } from '@angular/material/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { App } from './app';
import { AppRoutingModule } from './app-routing-module';
import { CoreModule } from './core/core.module';
import { SharedModule } from './features/shared/shared.module';

// Core Components
import { DashboardComponent } from './core/component/dashboard/dashboard.component';
import { LayoutComponent } from './core/component/layout/layout.component';
import { LoginComponent } from './core/component/login/login.component';
import { OauthCallbackComponent } from './core/component/oauth-callback/oauth-callback.component';

@NgModule({
  declarations: [
    App,
    LayoutComponent,
    LoginComponent,
    OauthCallbackComponent,
    DashboardComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    SharedModule,
    CoreModule,
  ],
  providers: [
    provideHttpClient(withInterceptorsFromDi()),
    provideBrowserGlobalErrorListeners(),
    provideNativeDateAdapter(),
  ],
  bootstrap: [App]
})
export class AppModule { }
