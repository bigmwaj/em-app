import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { User } from '../models/user.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser: Observable<User | null>;
  private readonly API_URL = environment.apiUrl;

  constructor(private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<User | null>(null);
    this.currentUser = this.currentUserSubject.asObservable();
    
    // Check if user is already authenticated
    if (this.getToken()) {
      this.loadCurrentUser();
    }
  }

  public get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  login(provider: 'google' | 'github' | 'facebook' | 'tiktok'): void {
    // Redirect to backend OAuth2 endpoint
    window.location.href = `${this.API_URL}/oauth2/authorization/${provider}`;
  }

  handleOAuthCallback(token: string): void {
    this.setToken(token);
    this.loadCurrentUser();
  }

  private loadCurrentUser(): void {
    this.http.get<User>(`${this.API_URL}/auth/user`).pipe(
      tap(user => this.currentUserSubject.next(user))
    ).subscribe({
      error: () => {
        this.logout();
      }
    });
  }

  logout(): void {
    this.removeToken();
    this.currentUserSubject.next(null);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getToken(): string | null {
    // NOTE: Storing JWT in localStorage is vulnerable to XSS attacks.
    // For production, consider using httpOnly cookies for better security.
    return localStorage.getItem('auth_token');
  }

  private setToken(token: string): void {
    // NOTE: Storing JWT in localStorage is vulnerable to XSS attacks.
    // For production, consider using httpOnly cookies for better security.
    localStorage.setItem('auth_token', token);
  }

  private removeToken(): void {
    localStorage.removeItem('auth_token');
  }
}
