import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { AuthUserInfo } from '../model/user.model';
import { SessionStorageService } from './session-storage.service';

interface LoginResponse {
  token: string;
  tokenType: string;
  expiresIn: number;
  user: {
    username: string;
    email: string;
    name: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject: BehaviorSubject<AuthUserInfo | null>;
  public currentUser: Observable<AuthUserInfo | null>;

  constructor(
    private sessionStorage: SessionStorageService,
    private http: HttpClient,
    private router: Router
  ) {
    // Initialize with token from localStorage if available
    const token = this.getToken();
    this.currentUserSubject = new BehaviorSubject<AuthUserInfo | null>(null);
    this.currentUser = this.currentUserSubject.asObservable();

    // Load user info if token exists
    if (token) {
      this.loadUserInfo().subscribe();
    }
  }

  public get currentUserValue(): AuthUserInfo | null {
    return this.currentUserSubject.value;
  }

  /**
   * Logs in with username and password
   * @param username the username (email)
   * @param password the password
   * @returns Observable of user info
   */
  loginWithCredentials(username: string, password: string): Observable<AuthUserInfo> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/auth/login`, {
      username,
      password
    }).pipe(
      tap(response => {
        this.setToken(response.token);
      }),
      tap(() => this.loadUserInfo().subscribe()),
      catchError(error => {
        return throwError(() => error);
      })
    );
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
  handleOAuthCallback(token: string): Observable<AuthUserInfo> {
    this.setToken(token);
    return this.loadUserInfo();
  }

  /**
   * Loads current user information from the backend
   */
  private loadUserInfo(): Observable<AuthUserInfo> {
    var token = this.getToken();
    if(!token){
      return throwError(() => new Error("Token does not exist"));
    }

    return this.http.get<AuthUserInfo>(`${environment.apiUrl}/auth/user`).pipe(
      tap(user => this.currentUserSubject.next(user)),
      catchError(error => {
        this.logout();
        return throwError(() => error);
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
