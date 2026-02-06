import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { User } from '../../../core/models/user.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-layout',
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss'],
  standalone: false
})
export class LayoutComponent implements OnInit, OnDestroy {
  user: User | null = null;
  sidenavOpened = true;
  private userSubscription?: Subscription;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Subscribe to current user
    this.userSubscription = this.authService.currentUser.subscribe(user => {
      this.user = user;
    });
  }

  ngOnDestroy(): void {
    // Clean up subscription to prevent memory leaks
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  /**
   * Logs out the current user
   */
  logout(): void {
    this.authService.logout();
  }

  /**
   * Toggles the sidenav
   */
  toggleSidenav(): void {
    this.sidenavOpened = !this.sidenavOpened;
  }
}
