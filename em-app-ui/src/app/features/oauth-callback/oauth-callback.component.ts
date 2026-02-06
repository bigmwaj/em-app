import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-oauth-callback',
  templateUrl: './oauth-callback.component.html',
  styleUrls: ['./oauth-callback.component.scss'],
  standalone: false
})
export class OauthCallbackComponent implements OnInit, OnDestroy {
  error: string | null = null;
  loading = true;
  private queryParamsSubscription?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Extract token from query parameters
    this.queryParamsSubscription = this.route.queryParams.subscribe(params => {
      const token = params['token'];
      const error = params['error'];

      if (error) {
        this.error = error;
        this.loading = false;
        return;
      }

      if (token) {
        // Handle OAuth callback with token
        this.authService.handleOAuthCallback(token).subscribe({
          next: (user) => {
            // Successfully authenticated, redirect to dashboard
            this.router.navigate(['/dashboard']);
          },
          error: (err) => {
            console.error('OAuth callback error:', err);
            this.error = 'Authentication failed. Please try again.';
            this.loading = false;
          }
        });
      } else {
        this.error = 'No token received from authentication provider.';
        this.loading = false;
      }
    });
  }

  ngOnDestroy(): void {
    // Clean up subscription to prevent memory leaks
    if (this.queryParamsSubscription) {
      this.queryParamsSubscription.unsubscribe();
    }
  }

  /**
   * Retry authentication by redirecting to login page
   */
  retry(): void {
    this.router.navigate(['/login']);
  }
}
