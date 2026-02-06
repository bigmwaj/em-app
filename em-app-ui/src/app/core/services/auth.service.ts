import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { User, AuthResponse } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser: Observable<User | null>;
  private readonly TOKEN_KEY = 'auth_token';

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    // Initialize with token from localStorage if available
    const token = this.getToken();
    this.currentUserSubject = new BehaviorSubject<User | null>(null);
    this.currentUser = this.currentUserSubject.asObservable();

    // Load user info if token exists
    if (token) {
      this.loadUserInfo().subscribe();
    }
  }

  public get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  /**
   * Initiates OAuth login by redirecting to backend OAuth endpoint
   * @param provider OAuth provider name (google, github, facebook, tiktok)
   */
  login(provider: string): void {
    window.location.href = `${environment.apiUrl}/oauth2/authorization/${provider}`;
  }

  /**
   * Handles OAuth callback by storing token and loading user info
   * @param token JWT token received from OAuth callback
   */
  handleOAuthCallback(token: string): Observable<User> {
    this.setToken(token);
    return this.loadUserInfo();
  }

  /**
   * Loads current user information from the backend
   */
  loadUserInfo(): Observable<User> {
    return this.http.get<User>(`${environment.apiUrl}/auth/user`).pipe(
      tap(user => this.currentUserSubject.next(user)),
      catchError(error => {
        console.error('Failed to load user info:', error);
        this.logout();
        throw error; // Re-throw error for caller to handle
      })
    );
  }

  /**
   * Logs out the current user and clears authentication data
   */
  logout(): void {
    this.removeToken();
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  /**
   * Checks if user is authenticated
   */
  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  /**
   * Gets the stored JWT token
   */
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Stores the JWT token
   */
  private setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  /**
   * Removes the JWT token
   */
  private removeToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
  }
}
