# Elite Maintenance App - Angular Frontend

A modern, scalable Angular web application with JWT authentication and Material Design UI.

## Features

- **Angular 21** - Latest stable version with TypeScript strict mode
- **Angular Material** - Material Design UI components
- **JWT Authentication** - Secure REST-based authentication
- **OAuth 2.0** - Support for Google, GitHub, Facebook, and TikTok
- **Modular Architecture** - Clean separation (core, shared, features)
- **Responsive Design** - Mobile-first, works on desktop and mobile
- **HTTP Interceptors** - Automatic JWT token attachment and error handling
- **Route Guards** - Protected routes with authentication

## Project Structure

```
em-app-ui/
├── src/
│   ├── app/
│   │   ├── core/                    # Singleton services and utilities
│   │   │   ├── guards/
│   │   │   │   └── auth.guard.ts   # Route protection
│   │   │   ├── interceptors/
│   │   │   │   ├── jwt.interceptor.ts    # JWT token injection
│   │   │   │   └── error.interceptor.ts  # Global error handling
│   │   │   ├── models/
│   │   │   │   ├── user.model.ts         # User interfaces
│   │   │   │   └── api.model.ts          # API response types
│   │   │   └── services/
│   │   │       ├── auth.service.ts       # Authentication logic
│   │   │       ├── user.service.ts       # User API calls
│   │   │       ├── account.service.ts    # Account API calls
│   │   │       └── contact.service.ts    # Contact API calls
│   │   ├── features/                # Feature modules
│   │   │   ├── login/              # Login page with OAuth
│   │   │   ├── oauth-callback/     # OAuth callback handler
│   │   │   ├── dashboard/          # Main dashboard
│   │   │   ├── users/              # User management
│   │   │   ├── accounts/           # Account management
│   │   │   └── contacts/           # Contact management
│   │   └── shared/                  # Shared components
│   │       └── components/
│   │           └── layout/         # Main layout (header + sidebar)
│   ├── environments/
│   │   ├── environment.ts          # Development config
│   │   └── environment.prod.ts     # Production config
│   └── styles.scss                 # Global styles
├── angular.json                     # Angular CLI configuration
├── package.json                     # Dependencies
└── tsconfig.json                    # TypeScript configuration
```

## Prerequisites

- Node.js 18.x or higher
- npm 9.x or higher
- Angular CLI 21.x (optional, can use npx)

## Installation

```bash
# Navigate to the UI directory
cd em-app-ui

# Install dependencies
npm install
```

## Development Server

```bash
# Start the development server
npm start

# Or with Angular CLI
ng serve
```

Navigate to `http://localhost:4200/`. The application will automatically reload if you change any source files.

## Build

```bash
# Build for development
npm run build

# Build for production
ng build --configuration=production
```

The build artifacts will be stored in the `dist/` directory.

## Configuration

### Environment Variables

Update `src/environments/environment.ts` for development:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'  // Backend API URL
};
```

Update `src/environments/environment.prod.ts` for production:

```typescript
export const environment = {
  production: true,
  apiUrl: '/api'  // Production API URL or relative path
};
```

### Backend Integration

The application expects a REST API backend running on `http://localhost:8080` with the following endpoints:

#### Authentication Endpoints
- `POST /oauth2/authorization/{provider}` - Initiate OAuth login
- `GET /oauth/callback?token={jwt}` - OAuth callback redirect
- `GET /auth/user` - Get current user info
- `GET /auth/status` - Check authentication status

#### Resource Endpoints
- `/api/v1/platform/user` - User CRUD operations
- `/api/v1/platform/account` - Account CRUD operations
- `/api/v1/platform/contact` - Contact CRUD operations

## Authentication Flow

1. **User clicks OAuth button** (Google, GitHub, Facebook, TikTok)
2. **Redirect to backend** `/oauth2/authorization/{provider}`
3. **Backend initiates OAuth** flow with the provider
4. **User authenticates** with the provider
5. **Provider redirects back** to backend with authorization code
6. **Backend generates JWT** token and redirects to `/oauth/callback?token={jwt}`
7. **Frontend stores token** in localStorage
8. **Subsequent API calls** include `Authorization: Bearer {token}` header
9. **JWT interceptor** automatically adds token to all HTTP requests
10. **Error interceptor** handles 401 errors and triggers logout

## Key Components

### AuthService

Manages authentication state using RxJS BehaviorSubject.

```typescript
// Login with OAuth provider
authService.login('google');

// Check if user is authenticated
authService.isAuthenticated();

// Get current user
authService.currentUser.subscribe(user => {
  console.log(user);
});

// Logout
authService.logout();
```

### HTTP Interceptors

- **JwtInterceptor**: Automatically adds JWT token to all outgoing requests
- **ErrorInterceptor**: Catches 401 errors and triggers logout

### AuthGuard

Protects routes requiring authentication. Redirects to login if not authenticated.

```typescript
// Protected route configuration
{
  path: 'dashboard',
  component: DashboardComponent,
  canActivate: [AuthGuard]
}
```

## Pages

### Login Page (`/login`)
- Clean gradient design
- Four OAuth provider buttons
- Auto-redirects if already authenticated

### OAuth Callback (`/oauth/callback`)
- Handles OAuth redirect
- Extracts JWT token
- Shows loading spinner
- Error handling with retry

### Dashboard (`/dashboard`)
- Welcome card with user info
- Quick action cards for:
  - Users Management
  - Accounts Management
  - Contacts Management
  - Security Status

### Management Pages
- **Users** (`/users`): Grid view of users with CRUD actions
- **Accounts** (`/accounts`): Grid view of accounts with status badges
- **Contacts** (`/contacts`): Grid view of contacts with info

### Layout
- Responsive Material sidenav
- Top toolbar with user menu
- Side navigation with icons
- Active route highlighting
- Logout action

## Code Quality

### TypeScript Strict Mode

The project uses TypeScript strict mode for maximum type safety:

```json
{
  "compilerOptions": {
    "strict": true,
    "noImplicitOverride": true,
    "noPropertyAccessFromIndexSignature": true,
    "noImplicitReturns": true,
    "noFallthroughCasesInSwitch": true
  }
}
```

### Angular Best Practices

- ✅ Modular architecture (core, shared, features)
- ✅ Single responsibility principle
- ✅ Proper use of RxJS (no nested subscriptions)
- ✅ HTTP interceptors for cross-cutting concerns
- ✅ Route guards for authorization
- ✅ Typed HTTP requests with interfaces
- ✅ Environment-based configuration

## Testing

```bash
# Run unit tests
npm test

# Run linter
npm run lint
```

## Security

### Token Storage

Currently uses `localStorage` for JWT token storage. For production, consider:
- HttpOnly cookies (more secure, prevents XSS)
- Token refresh mechanism
- Token expiration handling

### CORS Configuration

Ensure backend allows requests from frontend origin:

```java
// Backend CORS config
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
    configuration.setAllowCredentials(true);
    return source;
}
```

## Troubleshooting

### Common Issues

#### 1. API Connection Failed
- Ensure backend is running on `http://localhost:8080`
- Check CORS configuration on backend
- Verify API URLs in `environment.ts`

#### 2. OAuth Login Not Working
- Check OAuth provider credentials are configured in backend
- Verify redirect URIs match in OAuth provider settings
- Check browser console for errors

#### 3. Token Expired
- The JWT token expires after a configured time (default 24 hours)
- User will be automatically logged out on next API call
- Implement token refresh for better UX

#### 4. Build Errors
- Run `npm install` to ensure all dependencies are installed
- Clear Angular cache: `rm -rf .angular/cache`
- Check Node.js version: `node --version`

## Production Deployment

### Build Optimizations

```bash
# Production build with optimizations
ng build --configuration=production
```

This enables:
- Ahead-of-Time (AOT) compilation
- Tree shaking
- Minification
- Dead code elimination

### Deployment Options

1. **Static Hosting** (Netlify, Vercel, GitHub Pages)
   - Deploy `dist/em-app-ui` folder
   - Configure redirect for Angular routing

2. **Docker Container**
   ```dockerfile
   FROM nginx:alpine
   COPY dist/em-app-ui /usr/share/nginx/html
   COPY nginx.conf /etc/nginx/conf.d/default.conf
   ```

3. **CDN Deployment**
   - Upload build artifacts to CDN
   - Update `base href` in `index.html`

### Production Checklist

- [ ] Update `apiUrl` in `environment.prod.ts`
- [ ] Configure OAuth redirect URIs for production domain
- [ ] Enable HTTPS
- [ ] Set up monitoring and error tracking
- [ ] Implement token refresh mechanism
- [ ] Add security headers (CSP, HSTS, etc.)
- [ ] Optimize bundle size
- [ ] Enable service worker for offline support (optional)

## Contributing

1. Follow Angular style guide
2. Write unit tests for new features
3. Update documentation
4. Use conventional commit messages

## License

Copyright © 2026 Elite Maintenance App
