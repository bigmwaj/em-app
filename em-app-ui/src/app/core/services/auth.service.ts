import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { User } from '../model/user.model';
import { SessionStorageService } from './session-storage.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser: Observable<User | null>;

  constructor(
    private sessionStorage: SessionStorageService,
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
  private loadUserInfo(): Observable<User> {
    var token = this.getToken();
    if(!token){
      throw Error("Le token n'existe pas!");
    }

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
  private getToken(): string | null {
    return this.sessionStorage.token;
  }

  /**
   * Stores the JWT token
   */
  private setToken(token: string): void {
    this.sessionStorage.token = token;
  }

  /**
   * Removes the JWT token
   */
  private removeToken(): void {
    this.sessionStorage.removeToken();
  }
}
