# Architecture Explanation

## Overview

This application implements a modern full-stack architecture with a Spring Boot backend and Angular Material frontend, connected via OAuth2 authentication.

## System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         User's Browser                           │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │              Angular 16+ SPA (Port 4200)                    │ │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │ │
│  │  │   Login      │  │  Dashboard   │  │   Users/     │    │ │
│  │  │   Page       │  │              │  │   Accounts/  │    │ │
│  │  │              │  │              │  │   Contacts   │    │ │
│  │  └──────────────┘  └──────────────┘  └──────────────┘    │ │
│  └────────────────────────────────────────────────────────────┘ │
└────────────────────────┬────────────────────────────────────────┘
                         │ HTTPS/REST API + JWT
                         │
┌────────────────────────▼────────────────────────────────────────┐
│              Spring Boot Backend (Port 8080)                     │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                    Security Layer                           │ │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │ │
│  │  │ JWT Filter   │→ │OAuth2 Config │→ │ CORS Config  │    │ │
│  │  └──────────────┘  └──────────────┘  └──────────────┘    │ │
│  └────────────────────────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                    REST Controllers                         │ │
│  │  /api/v1/platform/user, /account, /contact                 │ │
│  │  /auth/user, /auth/status                                  │ │
│  └────────────────────────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                    Business Logic                           │ │
│  │  Service Layer → DAO Layer → JPA Repositories              │ │
│  └────────────────────────────────────────────────────────────┘ │
└────────────────────────┬────────────────────────────────────────┘
                         │ JDBC
                         │
┌────────────────────────▼────────────────────────────────────────┐
│                      MySQL Database                              │
│  Users, Accounts, Contacts, Addresses, Phones, Emails          │
└──────────────────────────────────────────────────────────────────┘

External OAuth2 Providers:
┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐
│  Google  │  │  GitHub  │  │ Facebook │  │  TikTok  │
└──────────┘  └──────────┘  └──────────┘  └──────────┘
     ▲             ▲             ▲             ▲
     └─────────────┴─────────────┴─────────────┘
                   OAuth2 Flow
```

## Authentication Flow (OAuth2 + JWT)

### Step-by-Step Flow

1. **Initial Access**
   ```
   User → Angular App → Not Authenticated → Redirect to /login
   ```

2. **Provider Selection**
   ```
   User clicks "Sign in with Google" (or GitHub/Facebook/TikTok)
   Angular → window.location.href = "http://localhost:8080/oauth2/authorization/google"
   ```

3. **OAuth2 Authorization**
   ```
   Backend → Redirects to Google OAuth consent page
   User → Authenticates with Google credentials
   Google → Redirects back to: http://localhost:8080/login/oauth2/code/google?code=...
   ```

4. **Token Exchange**
   ```
   Backend → Receives authorization code from Google
   Backend → Exchanges code for access token with Google
   Backend → Retrieves user info from Google
   Backend → Generates JWT token with user info
   ```

5. **Callback to Frontend**
   ```
   Backend → Redirects to: http://localhost:4200/oauth/callback?token=<JWT>
   Angular → Stores JWT in localStorage
   Angular → Loads user info
   Angular → Redirects to /dashboard
   ```

6. **Subsequent API Calls**
   ```
   Angular HTTP Request → JWT Interceptor adds "Authorization: Bearer <JWT>"
   Backend → JWT Filter validates token
   Backend → Processes request if valid
   ```

## Frontend Architecture (Angular)

### Module Structure

```
src/app/
├── core/                          # Singleton services and app-wide utilities
│   ├── guards/
│   │   └── auth.guard.ts         # Route protection
│   ├── interceptors/
│   │   ├── jwt.interceptor.ts    # Add JWT to requests
│   │   └── error.interceptor.ts  # Global error handling
│   ├── models/
│   │   ├── user.model.ts         # User interfaces
│   │   └── api.model.ts          # API response interfaces
│   └── services/
│       ├── auth.service.ts       # Authentication logic
│       └── user.service.ts       # User API calls
├── features/                      # Feature modules
│   ├── login/                    # Login page with OAuth buttons
│   ├── oauth-callback/           # OAuth callback handler
│   ├── dashboard/                # Main dashboard
│   ├── users/                    # User management
│   ├── accounts/                 # Account management
│   └── contacts/                 # Contact management
└── shared/                        # Shared components
    └── components/
        └── layout/               # Main layout (toolbar + sidenav)
```

### Key Components

#### AuthService
- **Manages authentication state** using RxJS BehaviorSubject
- **OAuth login** - Redirects to backend OAuth endpoints
- **Token management** - Stores/retrieves JWT from localStorage
- **User state** - Observable currentUser for reactive UI updates

#### HTTP Interceptors
1. **JwtInterceptor**: Adds `Authorization: Bearer <token>` to all outgoing requests
2. **ErrorInterceptor**: Catches 401 errors and triggers logout

#### AuthGuard
- **Protects routes** requiring authentication
- **Redirects** unauthenticated users to login page
- **Preserves** return URL for post-login redirect

## Backend Architecture (Spring Boot)

### Security Configuration

```java
SecurityConfig.java
├── CSRF: Disabled (stateless JWT)
├── Session: Stateless (no server sessions)
├── CORS: Configured for localhost:4200
├── Public endpoints: /auth/**, /oauth2/**, /swagger-ui/**
├── Protected: All other /api/** endpoints
└── OAuth2 login with success handler
```

### Key Components

#### JwtTokenProvider
- **Generate JWT** from OAuth2 user info
- **Validate JWT** signature and expiration
- **Extract user** email from JWT claims
- Uses HMAC-SHA256 with 256-bit secret key

#### OAuth2AuthenticationSuccessHandler
- Called after successful OAuth2 authentication
- Generates JWT token from authenticated user
- Redirects to frontend with token as query parameter

#### JwtAuthenticationFilter
- Runs on every request before controller
- Extracts JWT from Authorization header
- Validates token and sets Spring Security context
- Allows access to protected endpoints

### OAuth2 Configuration

Four providers configured in `application.yml`:

1. **Google**
   - Scopes: email, profile
   - User info: name, email, picture

2. **GitHub**
   - Scopes: user:email, read:user
   - User info: login, email, name

3. **Facebook**
   - Scopes: email, public_profile
   - User info: name, email

4. **TikTok**
   - Scopes: user.info.basic
   - Custom provider configuration
   - User info endpoint: open.tiktokapis.com

## Data Flow Examples

### Login Flow
```
1. User clicks "Sign in with Google" → AuthService.login('google')
2. Angular sets window.location → backend/oauth2/authorization/google
3. Spring Security redirects → Google OAuth consent
4. User approves → Google redirects to backend/login/oauth2/code/google
5. Backend exchanges code → Gets access token from Google
6. Backend fetches user info → Creates JWT with user data
7. OAuth2AuthenticationSuccessHandler → Redirects to frontend/oauth/callback?token=JWT
8. OauthCallbackComponent → Extracts token, stores in localStorage
9. AuthService.handleOAuthCallback() → Loads user info
10. Router navigates → /dashboard (protected by AuthGuard)
```

### API Request Flow
```
1. Component calls UserService.getUsers()
2. HttpClient prepares GET request
3. JwtInterceptor intercepts → Adds "Authorization: Bearer <JWT>"
4. Request sent to backend
5. JwtAuthenticationFilter intercepts → Validates JWT
6. Spring Security authenticates request
7. UserController processes → Calls service layer
8. Service calls DAO → Queries database
9. Response returned with user data
10. Component receives data → Updates UI
```

## Security Features

### 1. JWT Token Security
- **Algorithm**: HMAC-SHA256
- **Key Size**: 256 bits minimum
- **Expiration**: 24 hours (configurable)
- **Claims**: subject (email), name, issued-at, expiration
- **Storage**: localStorage (consider httpOnly cookies for production)

### 2. CORS Configuration
- **Allowed Origins**: localhost:4200 (development)
- **Allowed Methods**: GET, POST, PUT, DELETE, PATCH, OPTIONS
- **Allowed Headers**: All
- **Credentials**: True (allows cookies)
- **Max Age**: 3600 seconds

### 3. Route Protection
- **Frontend**: AuthGuard checks for valid JWT before navigation
- **Backend**: Spring Security requires authentication for /api/** endpoints
- **Stateless**: No server-side sessions (scales horizontally)

### 4. Error Handling
- **401 Unauthorized**: Auto-logout and redirect to login
- **Token Expiration**: Detected on next API call, triggers re-authentication
- **Network Errors**: Displayed to user with retry option

## Technology Stack

### Frontend
- **Angular 16+**: Modern SPA framework
- **Angular Material**: Material Design components
- **RxJS**: Reactive programming for state management
- **TypeScript**: Type-safe JavaScript
- **SCSS**: Styling with variables and nesting

### Backend
- **Spring Boot 4.0.1**: Modern Java framework
- **Spring Security**: Authentication and authorization
- **Spring OAuth2 Client**: OAuth2 provider integration
- **JJWT 0.12.6**: JWT token generation and validation
- **JPA/Hibernate**: ORM for database access
- **MySQL 8+**: Relational database

## Environment Configuration

### Development
- Frontend: `http://localhost:4200`
- Backend: `http://localhost:8080`
- Database: `localhost:3306`
- OAuth Redirect: `http://localhost:4200/oauth/callback`

### Production (Recommended)
- Frontend: `https://app.yourdomain.com`
- Backend: `https://api.yourdomain.com`
- Database: Managed database service
- OAuth Redirect: `https://app.yourdomain.com/oauth/callback`
- JWT Storage: httpOnly cookies instead of localStorage
- CORS: Restrict to production domain only

## Scalability Considerations

1. **Stateless Architecture**: JWT tokens eliminate server-side sessions
2. **Horizontal Scaling**: Multiple backend instances can validate JWTs independently
3. **Database Connection Pool**: Configured for concurrent requests
4. **CDN Deployment**: Angular SPA can be served from CDN
5. **API Gateway**: Consider adding for rate limiting and caching

## Future Enhancements

1. **Refresh Tokens**: Implement token refresh without re-authentication
2. **Role-Based Access**: Add user roles and permissions
3. **Multi-Factor Auth**: Additional security layer
4. **Token Revocation**: Blacklist for compromised tokens
5. **API Rate Limiting**: Prevent abuse
6. **Audit Logging**: Track authentication and authorization events
7. **Progressive Web App**: Offline capability and mobile install
