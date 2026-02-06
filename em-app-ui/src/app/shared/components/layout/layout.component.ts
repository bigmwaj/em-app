import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { User } from '../../../core/models/user.model';

@Component({
  selector: 'app-layout',
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss'],
  standalone: false
})
export class LayoutComponent implements OnInit {
  user: User | null = null;
  sidenavOpened = true;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Subscribe to current user
    this.authService.currentUser.subscribe(user => {
      this.user = user;
    });
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
