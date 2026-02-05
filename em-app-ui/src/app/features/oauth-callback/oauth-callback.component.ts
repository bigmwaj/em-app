import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { Subscription } from 'rxjs';
import { take } from 'rxjs/operators';

@Component({
  selector: 'app-oauth-callback',
  templateUrl: './oauth-callback.component.html',
  styleUrls: ['./oauth-callback.component.scss']
})
export class OauthCallbackComponent implements OnInit {
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Use take(1) to automatically unsubscribe after first emission
    this.route.queryParams.pipe(take(1)).subscribe(params => {
      const token = params['token'];
      const error = params['error'];

      if (error) {
        this.error = error;
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 3000);
      } else if (token) {
        this.authService.handleOAuthCallback(token);
        this.router.navigate(['/dashboard']);
      } else {
        this.error = 'No authentication token received';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 3000);
      }
    });
  }
}
