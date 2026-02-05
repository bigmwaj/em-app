import { Component } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    // If already authenticated, redirect to dashboard
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/dashboard']);
    }
  }

  loginWithGoogle(): void {
    this.authService.login('google');
  }

  loginWithGitHub(): void {
    this.authService.login('github');
  }

  loginWithFacebook(): void {
    this.authService.login('facebook');
  }

  loginWithTikTok(): void {
    this.authService.login('tiktok');
  }
}
