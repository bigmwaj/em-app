# Authentication Architecture Documentation

**Project:** Elite Maintenance Application (em-app)  
**Date:** 2026-02-14  
**Version:** 2.0 (with Username/Password Authentication)

---

## Table of Contents

1. [Overview](#overview)
2. [Authentication Methods](#authentication-methods)
3. [Architecture](#architecture)
4. [Security Features](#security-features)
5. [Backend Implementation](#backend-implementation)
6. [Frontend Implementation](#frontend-implementation)
7. [API Endpoints](#api-endpoints)
8. [Token Management](#token-management)
9. [User Flow Diagrams](#user-flow-diagrams)
10. [Configuration](#configuration)
11. [Security Best Practices](#security-best-practices)
12. [Testing](#testing)
13. [Troubleshooting](#troubleshooting)

---

## 1. Overview

The Elite Maintenance Application implements a **dual authentication system** supporting:
- **OAuth 2.0** for third-party authentication (Google, GitHub, Facebook, TikTok)
- **Username/Password** for traditional authentication

Both methods result in a **JWT (JSON Web Token)** being issued for stateless session management.

### Key Features
- ✅ BCrypt password hashing (cost factor 10)
- ✅ JWT-based stateless authentication
- ✅ Automatic token expiration (24 hours default)
- ✅ CORS configuration for cross-origin requests
- ✅ Secure password storage (write-only in API)
- ✅ Role-based access control ready
- ✅ Multiple authentication providers

---

## 2. Authentication Methods

### 2.1 Username/Password Authentication

**Use Case:** Traditional login for users with local accounts

**Flow:**
```
User → Login Form → POST /auth/login → Validate Credentials → Generate JWT → Return Token
```

**Advantages:**
- No dependency on third-party providers
- Faster login (no OAuth redirects)
- Full control over user data

**Security:**
- Passwords hashed with BCrypt
- Minimum password length enforced
- Account locking on failed attempts (future enhancement)
- Rate limiting recommended

### 2.2 OAuth 2.0 Authentication

**Supported Providers:**
- **Google** - Most common provider
- **GitHub** - Developer-focused
- **Facebook** - Social login
- **TikTok** - Emerging platform

**Flow:**
```
User → OAuth Button → Redirect to Provider → User Authorizes → 
Provider Redirects → Backend Processes → Generate JWT → Frontend Receives Token
```

**Advantages:**
- No password management
- Leverage provider's security
- Single Sign-On (SSO) capability
- Trusted identity verification

---

## 3. Architecture

### 3.1 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      Angular Frontend                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ Login Form   │  │ OAuth Buttons│  │ Auth Guard   │      │
│  └──────┬───────┘  └──────┬───────┘  └──────────────┘      │
│         │                  │                                 │
│         └──────────┬───────┘                                 │
│                    │                                         │
│         ┌──────────▼─────────┐                              │
│         │   AuthService      │                              │
│         └──────────┬─────────┘                              │
│                    │                                         │
│         ┌──────────▼─────────┐                              │
│         │  JWT Interceptor   │                              │
│         └──────────┬─────────┘                              │
└────────────────────┼─────────────────────────────────────────┘
                     │ HTTP Requests (Authorization: Bearer TOKEN)
                     │
┌────────────────────▼─────────────────────────────────────────┐
│                  Spring Boot Backend                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │ Auth         │  │ Security     │  │ JWT Token    │       │
│  │ Controller   │  │ Config       │  │ Provider     │       │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘       │
│         │                  │                  │               │
│  ┌──────▼──────────────────▼──────────────────▼───┐          │
│  │         Authentication Filter                  │          │
│  └──────┬─────────────────────────────────────────┘          │
│         │                                                     │
│  ┌──────▼─────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ UserService    │  │ Password     │  │ OAuth2       │     │
│  │ (UserDetails)  │  │ Encoder      │  │ Handler      │     │
│  └──────┬─────────┘  └──────────────┘  └──────────────┘     │
│         │                                                     │
│  ┌──────▼─────────┐                                          │
│  │  UserDao       │                                          │
│  └──────┬─────────┘                                          │
│         │                                                     │
│  ┌──────▼─────────┐                                          │
│  │  MySQL DB      │                                          │
│  │  PLATFORM_USER │                                          │
│  └────────────────┘                                          │
└──────────────────────────────────────────────────────────────┘
```

### 3.2 Component Interaction

#### Backend Components

| Component | Responsibility |
|-----------|---------------|
| **AuthController** | Handles /auth/login endpoint, processes credentials |
| **SecurityConfig** | Configures Spring Security, defines protected routes, CORS |
| **JwtTokenProvider** | Generates and validates JWT tokens |
| **AuthenticationFilter** | Intercepts requests, validates JWT tokens |
| **UserService** | Implements UserDetailsService, loads users, hashes passwords |
| **PasswordEncoder** | BCrypt encoder for password hashing/verification |
| **OAuth2AuthenticationSuccessHandler** | Handles successful OAuth2 authentication |
| **PostOAuth2Authentication** | Processes OAuth2 user info, creates local user |

#### Frontend Components

| Component | Responsibility |
|-----------|---------------|
| **LoginComponent** | Displays login form and OAuth buttons |
| **AuthService** | Manages authentication state, API calls |
| **JwtInterceptor** | Attaches JWT token to outgoing requests |
| **ErrorInterceptor** | Handles 401 errors, triggers logout |
| **AuthGuard** | Protects routes requiring authentication |
| **SessionStorageService** | Stores JWT token securely |

---

## 4. Security Features

### 4.1 Password Security

**Hashing Algorithm:** BCrypt  
**Cost Factor:** 10 (default)  
**Salt:** Automatically generated per password

**Example Hash:**
```
$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
```

**Password Policy:**
- Minimum 6 characters (frontend validation)
- No maximum length (backend handles)
- Stored as write-only in DTOs (never returned in API responses)

**Implementation:**
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// UserService.java
entity.setPassword(passwordEncoder.encode(dto.getPassword()));
```

### 4.2 Token Security

**JWT Structure:**
```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "user@example.com",
    "name": "John Doe",
    "email": "user@example.com",
    "provider": "local",
    "iat": 1708032000,
    "exp": 1708118400
  },
  "signature": "..."
}
```

**Token Lifetime:** 24 hours (86400000 ms)  
**Storage:** sessionStorage (frontend)  
**Transmission:** Authorization header (`Bearer <token>`)

**Security Considerations:**
- ✅ Token signed with HMAC-SHA256
- ✅ Secret key stored in environment variable
- ✅ Tokens expire automatically
- ✅ No sensitive data in token payload
- ⚠️ Consider using httpOnly cookies for enhanced XSS protection

### 4.3 CORS Configuration

**Allowed Origins:**
- `http://localhost:4200` (Angular dev server)
- `http://localhost:3000` (Alternative dev port)

**Allowed Methods:**
- GET, POST, PUT, DELETE, PATCH, OPTIONS

**Allowed Headers:**
- All headers (`*`) - Consider whitelisting in production

**Allow Credentials:** true

**Preflight Cache:** 3600 seconds (1 hour)

### 4.4 CSRF Protection

**Status:** Disabled for JWT-based authentication

**Rationale:**
- JWT tokens in Authorization headers are not automatically sent by browsers
- Not vulnerable to CSRF attacks (unlike session cookies)
- Safe for REST API architecture

**Note:** If switching to cookie-based authentication, enable CSRF protection.

---

## 5. Backend Implementation

### 5.1 Login Endpoint

**Endpoint:** `POST /auth/login`

**Request Body:**
```json
{
  "username": "user@example.com",
  "password": "SecurePass123!"
}
```

**Success Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "username": "user@example.com",
    "email": "user@example.com",
    "name": "John Doe"
  }
}
```

**Error Response (401 Unauthorized):**
```json
null
```

**Implementation:**
```java
@PostMapping("/auth/login")
public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
    try {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(authentication);
        
        // Extract user info
        AuthenticatedUser userDetails = (AuthenticatedUser) authentication.getPrincipal();
        // ... build response
        
        return ResponseEntity.ok(response);
    } catch (BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
}
```

### 5.2 User Details Service

**Loads users from database and verifies credentials:**

```java
@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    var user = dao.findByUsernameIgnoreCase(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    
    boolean enabled = UserStatusLvo.ACTIVE.equals(user.getStatus());
    var authorities = Collections.singleton(new AuthenticatedUserGrantedAuthority("USER"));
    
    return new AuthenticatedUser(
        GlobalMapper.INSTANCE.toDto(user), 
        enabled, 
        true, // accountNonExpired
        true, // credentialsNonExpired
        true, // accountNonLocked
        authorities
    );
}
```

### 5.3 Password Hashing

**On User Creation:**
```java
public UserDto create(UserDto dto) {
    var entity = GlobalMapper.INSTANCE.toEntity(dto);
    entity.setContact(getContact(dto));
    
    // Hash password if provided
    if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
    }
    
    beforeCreateHistEntity(entity);
    return GlobalMapper.INSTANCE.toDto(dao.save(entity));
}
```

**On User Update:**
```java
public UserDto update(UserDto dto) {
    var entity = GlobalMapper.INSTANCE.toEntity(dto);
    
    // Hash password if provided and changed
    if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
    } else {
        // Preserve existing password if not provided
        var existingUser = dao.findById(dto.getId());
        existingUser.ifPresent(user -> entity.setPassword(user.getPassword()));
    }
    
    beforeUpdateHistEntity(entity);
    return GlobalMapper.INSTANCE.toDto(dao.save(entity));
}
```

### 5.4 Security Configuration

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> 
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/", "/error", "/auth/**", "/oauth2/**").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .anyRequest().authenticated()
        )
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo.userService(postOAuth2Authentication))
            .successHandler(oAuth2AuthenticationSuccessHandler)
        )
        .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
```

---

## 6. Frontend Implementation

### 6.1 Login Component

**Template (login.component.html):**
```html
<form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
  <mat-form-field appearance="outline">
    <mat-label>Email</mat-label>
    <input matInput type="email" formControlName="username" />
    <mat-error *ngIf="username?.hasError('required')">Email is required</mat-error>
    <mat-error *ngIf="username?.hasError('email')">Please enter a valid email</mat-error>
  </mat-form-field>

  <mat-form-field appearance="outline">
    <mat-label>Password</mat-label>
    <input matInput [type]="hidePassword ? 'password' : 'text'" formControlName="password" />
    <mat-error *ngIf="password?.hasError('required')">Password is required</mat-error>
    <mat-error *ngIf="password?.hasError('minlength')">Min 6 characters</mat-error>
  </mat-form-field>

  <button mat-raised-button color="primary" type="submit" [disabled]="isLoading">
    Sign In
  </button>
</form>
```

**Component (login.component.ts):**
```typescript
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  isLoading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private formBuilder: FormBuilder
  ) {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const { username, password } = this.loginForm.value;

    this.authService.loginWithCredentials(username, password).subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        this.isLoading = false;
        if (error.status === 401) {
          this.errorMessage = 'Invalid username or password';
        } else {
          this.errorMessage = 'An error occurred. Please try again.';
        }
      }
    });
  }
}
```

### 6.2 Auth Service

```typescript
export class AuthService {
  /**
   * Logs in with username and password
   */
  loginWithCredentials(username: string, password: string): Observable<AuthUserInfo> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/auth/login`, {
      username,
      password
    }).pipe(
      tap(response => this.setToken(response.token)),
      tap(() => this.loadUserInfo().subscribe()),
      catchError(error => throwError(() => error))
    );
  }

  /**
   * Loads current user information
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
}
```

### 6.3 JWT Interceptor

```typescript
export class JwtInterceptor implements HttpInterceptor {
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.sessionStorage.token;
    
    if (token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
    
    return next.handle(request);
  }
}
```

---

## 7. API Endpoints

### Authentication Endpoints

| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/auth/login` | No | Username/password login |
| GET | `/auth/user` | Yes | Get current user info |
| GET | `/auth/status` | No | Check authentication status |
| GET | `/oauth2/authorization/{provider}` | No | Initiate OAuth2 login |
| GET | `/login/oauth2/code/{provider}` | No | OAuth2 callback URL |

### Protected Endpoints

| Method | Endpoint Pattern | Description |
|--------|------------------|-------------|
| GET/POST/PUT/DELETE | `/api/v1/**` | All API endpoints require authentication |

### Public Endpoints

| Endpoint Pattern | Description |
|------------------|-------------|
| `/` | Root path |
| `/error` | Error page |
| `/favicon.ico` | Favicon |
| `/swagger-ui/**` | API documentation |
| `/v3/api-docs/**` | OpenAPI spec |
| `/actuator/health` | Health check |

---

## 8. Token Management

### 8.1 Token Generation

```java
public String generateTokenForUser(String username, String email, String name) {
    var now = new Date();
    var expiryDate = new Date(now.getTime() + jwtExpirationMs);

    return Jwts.builder()
            .subject(username)
            .claim("name", name)
            .claim("email", email)
            .claim("provider", "local")
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(getSigningKey())
            .compact();
}
```

### 8.2 Token Validation

```java
public boolean validateToken(String authToken) {
    try {
        Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(authToken);
        return true;
    } catch (Exception ex) {
        logger.debug("Invalid JWT token", ex);
    }
    return false;
}
```

### 8.3 Token Extraction

```java
public String getUsernameFromJWT(String token) {
    return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload().getSubject();
}
```

### 8.4 Token Storage (Frontend)

**Current Implementation:** sessionStorage

**Alternative (Recommended for Production):** httpOnly Secure Cookies

```typescript
// Current: sessionStorage
this.sessionStorage.token = token;

// Recommended: Backend sets httpOnly cookie
// Frontend: No direct token access
```

---

## 9. User Flow Diagrams

### 9.1 Username/Password Login Flow

```
┌─────────┐
│  User   │
└────┬────┘
     │
     │ 1. Enter email & password
     ▼
┌─────────────┐
│ LoginComponent│
└─────┬───────┘
      │
      │ 2. Form validation
      ▼
┌─────────────┐
│ AuthService │
└─────┬───────┘
      │
      │ 3. POST /auth/login
      ▼
┌──────────────┐
│AuthController│
└─────┬────────┘
      │
      │ 4. AuthenticationManager.authenticate()
      ▼
┌──────────────┐
│ UserService  │
└─────┬────────┘
      │
      │ 5. Load user from DB
      ▼
┌──────────────┐
│  UserDao     │
└─────┬────────┘
      │
      │ 6. Return UserEntity
      ▼
┌──────────────┐
│PasswordEncoder│
└─────┬────────┘
      │
      │ 7. Verify password hash
      ▼
┌──────────────┐
│JwtTokenProvider│
└─────┬────────┘
      │
      │ 8. Generate JWT token
      ▼
┌──────────────┐
│ Response     │
│ (200 OK)     │
└─────┬────────┘
      │
      │ 9. Return {token, user}
      ▼
┌─────────────┐
│ AuthService │
└─────┬───────┘
      │
      │ 10. Store token
      │ 11. Load user info
      ▼
┌─────────────┐
│  Dashboard  │
└─────────────┘
```

### 9.2 OAuth2 Login Flow

```
┌─────────┐
│  User   │
└────┬────┘
     │
     │ 1. Click "Sign in with Google"
     ▼
┌─────────────┐
│ LoginComponent│
└─────┬───────┘
      │
      │ 2. Redirect to /oauth2/authorization/google
      ▼
┌──────────────┐
│Spring Security│
└─────┬────────┘
      │
      │ 3. Redirect to Google OAuth
      ▼
┌──────────────┐
│ Google OAuth │
└─────┬────────┘
      │
      │ 4. User authorizes
      │ 5. Redirect to /login/oauth2/code/google
      ▼
┌──────────────┐
│Spring Security│
└─────┬────────┘
      │
      │ 6. Exchange code for token
      ▼
┌────────────────┐
│PostOAuth2Auth  │
└─────┬──────────┘
      │
      │ 7. Extract user info
      │ 8. Create/update local user
      ▼
┌────────────────┐
│OAuth2Success   │
│Handler         │
└─────┬──────────┘
      │
      │ 9. Generate JWT token
      │ 10. Redirect to frontend with token
      ▼
┌──────────────┐
│OAuth Callback│
│Component     │
└─────┬────────┘
      │
      │ 11. Extract token from URL
      │ 12. Store token
      ▼
┌─────────────┐
│  Dashboard  │
└─────────────┘
```

---

## 10. Configuration

### 10.1 Backend Configuration

**application.yml:**
```yaml
app:
  jwt:
    secret: ${JWT_SECRET}  # Required environment variable
    expiration: ${JWT_EXPIRATION:86400000}  # 24 hours default
  auth-success:
    redirect-uri: ${AUTH_SUCCESS_REDIRECT_URI:http://localhost:4200/oauth/callback}

spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope:
              - email
              - profile
          github:
            client-id: ${GITHUB_CLIENT_ID}
            client-secret: ${GITHUB_CLIENT_SECRET}
            scope:
              - user:email
              - read:user
          # ... more providers
```

### 10.2 Frontend Configuration

**environment.ts:**
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'
};
```

**environment.prod.ts:**
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://api.yourdomain.com'
};
```

### 10.3 Required Environment Variables

| Variable | Required | Description | Example |
|----------|----------|-------------|---------|
| `JWT_SECRET` | Yes | Secret key for JWT signing (64+ chars) | `openssl rand -base64 64` |
| `JWT_EXPIRATION` | No | Token lifetime in ms | `86400000` (24h) |
| `DB_URL` | Yes | MySQL connection string | `jdbc:mysql://localhost:3306/media_db` |
| `DB_USERNAME` | Yes | Database username | `media_db_user` |
| `DB_PASSWORD` | Yes | Database password | `SecurePassword123!` |
| `GOOGLE_CLIENT_ID` | Optional | Google OAuth client ID | - |
| `GOOGLE_CLIENT_SECRET` | Optional | Google OAuth secret | - |
| `GITHUB_CLIENT_ID` | Optional | GitHub OAuth client ID | - |
| `GITHUB_CLIENT_SECRET` | Optional | GitHub OAuth secret | - |

---

## 11. Security Best Practices

### ✅ Implemented

1. **BCrypt Password Hashing** - Passwords stored securely
2. **JWT Token Signing** - HMAC-SHA256 cryptographic signing
3. **Password Write-Only** - Never returned in API responses
4. **Token Expiration** - Tokens expire after 24 hours
5. **HTTPS Ready** - Can be configured for production
6. **Input Validation** - Email format, password length checks
7. **SQL Injection Protection** - JPA/Hibernate parameterized queries
8. **XSS Protection** - Angular sanitizes DOM by default

### ⚠️ Recommended Enhancements

1. **Rate Limiting** - Prevent brute force attacks
   ```java
   // Use Bucket4j or similar
   @RateLimit(permitsPerSecond = 5)
   ```

2. **Account Locking** - Lock after N failed attempts
   ```java
   if (failedAttempts >= 5) {
       user.setStatus(UserStatusLvo.LOCKED);
   }
   ```

3. **Token Refresh** - Implement refresh tokens
   ```java
   POST /auth/refresh
   Body: { refreshToken: "..." }
   ```

4. **HTTPS Enforcement** - Require HTTPS in production
   ```java
   http.requiresChannel(channel -> 
       channel.anyRequest().requiresSecure()
   );
   ```

5. **httpOnly Cookies** - Store JWT in httpOnly cookie
   ```java
   response.addCookie(new Cookie("AUTH_TOKEN", token) {{
       setHttpOnly(true);
       setSecure(true);
       setSameSite("Strict");
   }});
   ```

6. **CSRF for Cookies** - Enable if using cookie auth
   ```java
   .csrf(csrf -> csrf.csrfTokenRepository(
       CookieCsrfTokenRepository.withHttpOnlyFalse()
   ))
   ```

7. **Password Strength** - Enforce complexity rules
   ```typescript
   Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{8,}$/)
   ```

8. **Two-Factor Authentication (2FA)** - Add second factor
9. **Audit Logging** - Log authentication events
10. **Security Headers** - Add Content-Security-Policy, X-Frame-Options

---

## 12. Testing

### 12.1 Backend Tests

**Unit Test - UserService:**
```java
@Test
void whenCreateUser_thenPasswordIsHashed() {
    // Given
    UserDto dto = new UserDto();
    dto.setUsername("test@example.com");
    dto.setPassword("plaintext");
    
    // When
    UserDto result = userService.create(dto);
    
    // Then
    assertNotEquals("plaintext", result.getPassword());
    assertTrue(passwordEncoder.matches("plaintext", result.getPassword()));
}
```

**Integration Test - Login Endpoint:**
```java
@Test
void whenLoginWithValidCredentials_thenReturnsToken() throws Exception {
    // Given
    String username = "test@example.com";
    String password = "password123";
    createTestUser(username, password);
    
    // When & Then
    mockMvc.perform(post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists())
        .andExpect(jsonPath("$.tokenType").value("Bearer"));
}
```

### 12.2 Frontend Tests

**Unit Test - AuthService:**
```typescript
describe('AuthService', () => {
  it('should login with credentials and store token', (done) => {
    const mockResponse = {
      token: 'test.jwt.token',
      tokenType: 'Bearer',
      expiresIn: 86400000,
      user: { username: 'test@example.com', email: 'test@example.com', name: 'Test User' }
    };
    
    service.loginWithCredentials('test@example.com', 'password').subscribe(user => {
      expect(user).toBeTruthy();
      expect(service.isAuthenticated()).toBe(true);
      done();
    });
    
    const req = httpMock.expectOne('/api/auth/login');
    req.flush(mockResponse);
  });
});
```

**E2E Test - Login Flow:**
```typescript
describe('Login Flow', () => {
  it('should login successfully with username and password', () => {
    cy.visit('/login');
    cy.get('[formControlName="username"]').type('test@example.com');
    cy.get('[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();
    
    cy.url().should('include', '/dashboard');
    cy.get('.user-name').should('contain', 'Test User');
  });
});
```

---

## 13. Troubleshooting

### Common Issues

#### 1. "Invalid username or password"

**Cause:** Username doesn't exist or password is incorrect

**Solutions:**
- Verify user exists in database: `SELECT * FROM PLATFORM_USER WHERE USERNAME = 'user@example.com'`
- Check password hash: Passwords must be BCrypt hashed in database
- Verify password during registration: Ensure password was hashed when user was created

---

#### 2. "401 Unauthorized" on protected endpoints

**Cause:** Token not sent or invalid

**Solutions:**
- Check token is stored: `sessionStorage.getItem('auth_token')`
- Verify JWT Interceptor is attached: Check Authorization header in Network tab
- Validate token: Use jwt.io to decode and check expiration
- Check server logs for token validation errors

---

#### 3. CORS errors

**Cause:** Frontend origin not allowed by backend

**Solutions:**
- Update SecurityConfig allowed origins:
  ```java
  configuration.setAllowedOrigins(Arrays.asList(
      "http://localhost:4200",
      "https://yourdomain.com"
  ));
  ```
- Verify preflight OPTIONS requests return 200 OK
- Check CORS headers in response

---

#### 4. OAuth redirect doesn't work

**Cause:** Redirect URI mismatch

**Solutions:**
- Verify OAuth provider redirect URI: `{baseUrl}/login/oauth2/code/{provider}`
- Check frontend redirect URI in application.yml
- Ensure OAuth credentials are correct in environment variables

---

#### 5. Password not hashing

**Cause:** PasswordEncoder not injected or not used

**Solutions:**
- Verify PasswordEncoder bean exists in SecurityConfig
- Check UserService has `@Autowired PasswordEncoder`
- Ensure password is encoded before saving:
  ```java
  entity.setPassword(passwordEncoder.encode(dto.getPassword()));
  ```

---

#### 6. Token expires too quickly

**Cause:** JWT expiration set too low

**Solutions:**
- Check `JWT_EXPIRATION` environment variable
- Default is 24 hours (86400000 ms)
- Increase if needed: `JWT_EXPIRATION=172800000` (48 hours)

---

## Conclusion

This authentication system provides a **secure, flexible, and user-friendly** authentication experience supporting both traditional credentials and modern OAuth2 providers. The implementation follows industry best practices with BCrypt password hashing, JWT token management, and proper security configurations.

### Key Achievements
✅ Dual authentication (OAuth2 + Username/Password)  
✅ Secure password storage (BCrypt)  
✅ Stateless JWT authentication  
✅ Protection against common vulnerabilities  
✅ Production-ready architecture  

### Next Steps
1. Implement rate limiting
2. Add refresh token mechanism
3. Enable HTTPS in production
4. Add account locking after failed attempts
5. Implement 2FA
6. Add comprehensive audit logging

---

**Documentation Version:** 1.0  
**Last Updated:** 2026-02-14  
**Author:** GitHub Copilot AI  
**Review Status:** Complete
