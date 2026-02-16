import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AuthUserInfo } from '../../model/user.model';
import { Subscription } from 'rxjs';
import { SessionStorageService } from '../../services/session-storage.service';

@Component({
  selector: 'app-layout',
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss'],
  standalone: false
})
export class LayoutComponent implements OnInit, OnDestroy {
  user: AuthUserInfo | null = null;
  sidenavOpened = true;
  platformMenuExpanded = true;
  private userSubscription?: Subscription;

  constructor(
    private authService: AuthService,
    private sessionStorageService: SessionStorageService
  ) {}

  ngOnInit(): void {
    // Subscribe to current user
    this.userSubscription = this.authService.currentUser.subscribe(user => {
      this.user = user;
    });

    // Initialize sidenav state from session storage
    const storedSidenavState = this.sessionStorageService.sidenavOpened;
    if (storedSidenavState !== null) {
      this.sidenavOpened = storedSidenavState === 'true';
    }

    // Initialize platform menu state from session storage
    const storedPlatformMenuState = this.sessionStorageService.platformMenuExpanded;
    if (storedPlatformMenuState !== null) {
      this.platformMenuExpanded = storedPlatformMenuState === 'true';
    }
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
    this.sessionStorageService.sidenavOpened = this.sidenavOpened.toString();
  }

  /**
   * Handles platform menu expansion state changes
   */
  onPlatformMenuToggle(expanded: boolean): void {
    this.platformMenuExpanded = expanded;
    this.sessionStorageService.platformMenuExpanded = expanded.toString();
  }
}
