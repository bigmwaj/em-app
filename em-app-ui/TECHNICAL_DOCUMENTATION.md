# app-ui Technical Documentation (`em-app-ui`)

**Module:** `em-app-ui` — Angular Frontend Application  
**Type:** Angular Single-Page Application (SPA)  
**Version:** Angular 21.1.3 / TypeScript 5.9.2  
**Last Updated:** 2026-03-01  

---

## Table of Contents

1. [Technical Overview](#1-technical-overview)
2. [Module Architecture](#2-module-architecture)
3. [State Management Explanation](#3-state-management-explanation)
4. [API Integration Strategy](#4-api-integration-strategy)
5. [Build Process](#5-build-process)
6. [Environment Configuration](#6-environment-configuration)
7. [Deployment Process](#7-deployment-process)

---

## 1. Technical Overview

`em-app-ui` is the Angular frontend for the Elite Maintenance Application. It provides a Material Design user interface for authenticating users, managing platform data (Users, Accounts, Contacts, Groups, Roles, Privileges), and handling OAuth2 callbacks.

### Technology Stack

| Layer | Technology |
|-------|-----------|
| Framework | Angular 21.1.3 |
| Language | TypeScript 5.9.2 (strict mode) |
| UI Library | Angular Material 21.1.3 + CDK |
| Reactive | RxJS 7.8.0 |
| Testing | Vitest 4.0.8 + @analogjs/vitest-angular |
| Linting | ESLint (Angular config) |
| Formatting | Prettier |
| Build | Angular CLI 21 (esbuild) |
| Package Manager | npm |

### TypeScript Configuration

The project uses **strict TypeScript mode** with additional constraints:

```json
{
  "compilerOptions": {
    "strict": true,
    "noImplicitOverride": true,
    "noPropertyAccessFromIndexSignature": true,
    "noImplicitReturns": true,
    "noFallthroughCasesInSwitch": true,
    "target": "ES2022"
  }
}
```

---

## 2. Module Architecture

### Directory Structure

```
em-app-ui/
├── src/
│   ├── app/
│   │   ├── app.ts                     # Root component
│   │   ├── app.html                   # Root template
│   │   ├── app-module.ts              # Root NgModule
│   │   ├── app-routing-module.ts      # Top-level routing
│   │   │
│   │   ├── core/                      # Singleton services (loaded once)
│   │   │   ├── core.module.ts
│   │   │   ├── component/
│   │   │   │   ├── dashboard/         # Main dashboard page
│   │   │   │   ├── layout/            # App shell (header + sidenav)
│   │   │   │   ├── login/             # Login page (username/password + OAuth)
│   │   │   │   └── oauth-callback/    # OAuth2 token receiver page
│   │   │   ├── guards/
│   │   │   │   └── auth.guard.ts      # Route protection
│   │   │   ├── interceptors/
│   │   │   │   ├── jwt.interceptor.ts      # Adds Authorization header
│   │   │   │   └── error.interceptor.ts    # Handles 401 → auto-logout
│   │   │   ├── model/
│   │   │   │   └── user.model.ts      # AuthUserInfo interface
│   │   │   └── services/
│   │   │       ├── auth.service.ts          # Login, logout, OAuth handling
│   │   │       └── session-storage.service.ts  # localStorage wrapper
│   │   │
│   │   └── features/
│   │       ├── shared/                # Shared feature utilities
│   │       │   ├── shared.module.ts
│   │       │   ├── api.shared.model.ts        # Shared API response types
│   │       │   ├── common.datasource.ts       # Angular CDK DataSource<T>
│   │       │   ├── shared.helper.ts
│   │       │   └── component/
│   │       │       ├── abstract-edit.component.ts        # Base edit component
│   │       │       ├── abstract-edit-with-status.component.ts
│   │       │       ├── abstract-index.component.ts       # Base list component
│   │       │       ├── abstract-index-with-status.component.ts
│   │       │       ├── change-status-dialog.component.ts
│   │       │       ├── delete-dialog.component.ts
│   │       │       ├── loading.component.ts
│   │       │       ├── message.component.ts
│   │       │       └── search-form.component.ts
│   │       │
│   │       └── platform/              # Platform feature module (lazy-loaded)
│   │           ├── platform.module.ts
│   │           ├── api.platform.model.ts      # Platform API types
│   │           ├── platform.helper.ts
│   │           ├── constants/
│   │           │   └── country.constants.ts
│   │           ├── helper/            # Per-entity form helpers
│   │           │   ├── user.helper.ts
│   │           │   ├── account.helper.ts
│   │           │   ├── contact.helper.ts
│   │           │   ├── group.helper.ts
│   │           │   ├── role.helper.ts
│   │           │   ├── privilege.helper.ts
│   │           │   └── dead-letter.helper.ts
│   │           ├── service/           # Domain HTTP services
│   │           │   ├── user.service.ts
│   │           │   ├── account.service.ts
│   │           │   ├── contact.service.ts
│   │           │   ├── group.service.ts
│   │           │   ├── role.service.ts
│   │           │   ├── privilege.service.ts
│   │           │   └── dead-letter.service.ts
│   │           └── component/         # Feature UI components
│   │               ├── user/
│   │               ├── account/
│   │               ├── contact/
│   │               ├── group/
│   │               ├── role/
│   │               ├── dead-letter/
│   │               └── shared/        # Cross-entity components (user assign lists)
│   │
│   ├── environments/
│   │   ├── environment.ts             # Development config
│   │   └── environment.prod.ts        # Production config
│   ├── index.html                     # App entry HTML
│   ├── main.ts                        # Bootstrap
│   └── styles.scss                    # Global styles
│
├── angular.json                        # Angular CLI workspace config
├── package.json                        # npm dependencies
├── tsconfig.json                       # TypeScript root config
├── tsconfig.app.json                   # App-specific TypeScript config
└── tsconfig.spec.json                  # Test TypeScript config
```

---

### Module Descriptions

#### `CoreModule`

Loaded once at application startup by `AppModule`. Provides all singleton services, guards, and interceptors. Guards against multiple imports to prevent duplicate provider instances.

**Provided singletons:**
- `AuthService` — Authentication state and operations
- `SessionStorageService` — localStorage abstraction
- `JwtInterceptor` — HTTP interceptor for token injection
- `ErrorInterceptor` — HTTP interceptor for 401 handling
- `AuthGuard` — Route guard

**Components in Core (not lazy-loaded):**
- `LoginComponent` — `/login`
- `OAuthCallbackComponent` — `/oauth/callback`
- `DashboardComponent` — `/dashboard`
- `LayoutComponent` — App shell wrapping all protected pages

---

#### `SharedModule` (`features/shared/`)

Provides reusable UI components and base classes for all feature modules. Not a lazy-loaded module — imported by `PlatformModule`.

**Key exports:**

| Class | Type | Description |
|-------|------|-------------|
| `AbstractEditComponent<T>` | Abstract Component | Base for CREATE / EDIT / VIEW / DUPLICATE pages |
| `AbstractIndexComponent<T>` | Abstract Component | Base for paginated, searchable list pages |
| `AbstractEditWithStatusComponent<T>` | Abstract Component | Edit variant with status management |
| `AbstractIndexWithStatusComponent<T>` | Abstract Component | Index variant with status management |
| `CommonDataSource<T>` | DataSource | Angular CDK DataSource for Material tables |
| `DeleteDialogComponent` | Dialog | Reusable delete-confirmation dialog |
| `ChangeStatusDialogComponent` | Dialog | Status change confirmation dialog |
| `LoadingComponent` | Component | Spinner/loading overlay |
| `MessageComponent` | Component | Success/error/warning message display |
| `SearchFormComponent` | Component | Configurable search form |

---

#### `PlatformModule` (`features/platform/`)

Lazy-loaded feature module for all platform domain management. Contains all CRUD UI for: Users, Accounts, Contacts, Groups, Roles, Privileges, Dead Letters.

**Pattern for each domain:**

```
/platform/{entity}              → IndexComponent (list/search)
/platform/{entity}/create       → EditComponent (create mode)
/platform/{entity}/edit/:id     → EditComponent (edit mode)
/platform/{entity}/view/:id     → EditComponent (view mode)
```

---

### Component Inheritance Chain

```
AbstractIndexComponent<T>
    ↑
AbstractIndexWithStatusComponent<T>    ← adds status-specific actions
    ↑
UserIndexComponent                     ← binds to UserService

AbstractEditComponent<T>
    ↑
AbstractEditWithStatusComponent<T>     ← adds status change dialog
    ↑
UserEditComponent                      ← UserDto form definition
```

This inheritance chain avoids duplicating pagination, sorting, search, form validation, and dialog-triggering logic across every domain feature.

---

### Routing Structure

```typescript
// app-routing-module.ts

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'oauth/callback', component: OAuthCallbackComponent },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: 'dashboard', component: DashboardComponent },
      {
        path: 'platform',
        loadChildren: () =>
          import('./features/platform/platform.module').then(m => m.PlatformModule)
      }
    ]
  },
  { path: '**', redirectTo: 'login' }
];
```

All platform routes are protected by `AuthGuard`. Unauthenticated access redirects to `/login` with `returnUrl` preserved as a query parameter.

---

## 3. State Management Explanation

### Current Strategy: Service-Based State with RxJS

The application does **not** use a formal state management library (NgRx, Akita, etc.). State is managed at two levels:

#### Authentication State (`AuthService`)

```typescript
// Centralized auth state
private currentUserSubject = new BehaviorSubject<AuthUserInfo | null>(null);
public currentUser$ = this.currentUserSubject.asObservable();
```

- `currentUser$` observable is the single source of truth for the logged-in user.
- Components subscribe to `currentUser$` for user-specific rendering.
- On login, `currentUserSubject.next(user)` broadcasts the new user to all subscribers.
- On logout, `currentUserSubject.next(null)` clears state and `router.navigate(['/login'])` redirects.

#### Token Persistence (`SessionStorageService`)

Authentication tokens are persisted in `localStorage` (under the key `token_key`) across browser sessions. On application startup, `AuthService` reads the stored token and restores the auth state.

#### Component-Level State

Feature components (lists, edit forms) manage their own local state:
- List components use `CommonDataSource<T>` which wraps an `Observable<T[]>` for Angular Material tables.
- Edit components hold the current form model locally and interact with domain services directly.

#### State Management Limitations and Roadmap

| Limitation | Recommended Solution |
|------------|---------------------|
| No shared domain data cache | Add NgRx or Angular Signal Store |
| Manual subscription management in components | Migrate to `async` pipe + `OnPush` |
| No optimistic updates | Requires state management library |
| Page reload loses unsaved form data | Requires form state persistence |

---

## 4. API Integration Strategy

### HTTP Client Setup

All API calls use Angular's `HttpClient`. The base API URL is injected from the environment configuration:

```typescript
// environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'
};
```

### Interceptors

#### `JwtInterceptor`

Automatically adds the `Authorization: Bearer {token}` header to every outgoing HTTP request:

```typescript
// jwt.interceptor.ts
const token = this.sessionStorageService.token;
if (token) {
  request = request.clone({
    setHeaders: { Authorization: `Bearer ${token}` }
  });
}
return next(request);
```

Applied globally via `HTTP_INTERCEPTORS` provider in `CoreModule`.

#### `ErrorInterceptor`

Catches HTTP `401 Unauthorized` responses and triggers an automatic logout:

```typescript
// error.interceptor.ts
if (error.status === 401) {
  this.authService.logout();
  this.router.navigate(['/logout']);
}
```

Applied globally. All other error codes propagate to the calling component.

---

### Domain Service Pattern

Each platform domain has a dedicated service. Example pattern (`UserService`):

```typescript
@Injectable({ providedIn: PlatformModule })
export class UserService {
  private apiUrl = `${environment.apiUrl}/api/v1/platform/users`;

  constructor(private http: HttpClient) {}

  getUsers(criteria?: UserSearchCriteria): Observable<SearchResultDto<UserDto>> {
    const params = this.buildParams(criteria);
    return this.http.get<SearchResultDto<UserDto>>(this.apiUrl, { params });
  }

  getUser(id: number): Observable<ResponseMessage<UserDto>> {
    return this.http.get<ResponseMessage<UserDto>>(`${this.apiUrl}/id/${id}`);
  }

  createUser(dto: UserDto): Observable<ResponseMessage<UserDto>> {
    return this.http.post<ResponseMessage<UserDto>>(this.apiUrl, dto);
  }

  updateUser(dto: UserDto): Observable<ResponseMessage<UserDto>> {
    return this.http.patch<ResponseMessage<UserDto>>(this.apiUrl, dto);
  }

  deleteUser(id: number): Observable<Message> {
    return this.http.delete<Message>(`${this.apiUrl}/${id}`);
  }
}
```

All services follow this identical structure with entity-specific types. The same pattern is replicated for `AccountService`, `ContactService`, `GroupService`, `RoleService`, `PrivilegeService`, and `DeadLetterService`.

---

### Authentication API Integration

#### Username/Password Login

```typescript
// auth.service.ts
loginWithCredentials(username: string, password: string): Observable<LoginResponse> {
  return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, { username, password })
    .pipe(
      tap(response => {
        this.sessionStorageService.token = response.token;
        this.loadUserInfo();
      })
    );
}
```

#### OAuth2 Login

```typescript
// Redirect browser to backend OAuth2 authorization endpoint
login(provider: 'google' | 'github' | 'facebook'): void {
  window.location.href = `${environment.apiUrl}/oauth2/authorization/${provider}`;
}
```

#### OAuth2 Callback Handling

```typescript
// oauth-callback.component.ts / auth.service.ts
handleOAuthCallback(token: string): void {
  this.sessionStorageService.token = token;
  this.loadUserInfo().subscribe(() => {
    this.router.navigate(['/dashboard']);
  });
}
```

---

### API Response Types

```typescript
// api.shared.model.ts
export interface SearchResultDto<T> {
  data: T[];
  searchInfos: SearchInfos;
}

export interface SearchInfos {
  total?: number;
  pageSize: number;
  pageIndex: number;
}

export interface ResponseMessage<T> {
  data: T;
}

export interface Message {
  type: 'SUCCESS' | 'WARNING' | 'ERROR';
  text: string;
}
```

---

## 5. Build Process

### Install Dependencies

```bash
cd em-app-ui
npm install
```

### Development Build + Live Reload

```bash
npm start
# or
ng serve
```

Starts the development server at `http://localhost:4200` with hot module replacement.

### Production Build

```bash
npm run build
# or
ng build --configuration=production
```

Output is placed in `dist/em-app-ui/browser/`.

**Production build optimizations (Angular CLI defaults):**
- Ahead-of-Time (AOT) compilation
- Tree shaking
- JavaScript minification and mangling
- CSS minification
- Dead code elimination
- Source map generation (configurable)

### Run Tests

```bash
# Run unit tests once
npm test

# Run tests in watch mode
npm run test:watch

# Run tests with coverage
npm run test:coverage
```

Tests use **Vitest** with the `@analogjs/vitest-angular` adapter for Angular component compatibility.

### Lint

```bash
npm run lint
```

### Format

```bash
npm run format
```

### Available npm Scripts

| Script | Command | Description |
|--------|---------|-------------|
| `start` | `ng serve` | Start dev server |
| `build` | `ng build` | Production build |
| `test` | `ng test` | Run tests with Vitest |
| `lint` | `ng lint` | Run ESLint |
| `format` | `prettier --write .` | Format all files |

---

## 6. Environment Configuration

### Development (`src/environments/environment.ts`)

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'
};
```

### Production (`src/environments/environment.prod.ts`)

```typescript
export const environment = {
  production: true,
  apiUrl: '/api'
};
```

In production, `apiUrl: '/api'` assumes a reverse proxy (e.g., Nginx) routes `/api` requests to the backend service. This avoids hardcoding the backend hostname in the deployed frontend bundle.

### Angular CLI Environment Replacement

Angular CLI replaces `environment.ts` with `environment.prod.ts` during production builds automatically:

```json
// angular.json (excerpt)
"configurations": {
  "production": {
    "fileReplacements": [
      {
        "replace": "src/environments/environment.ts",
        "with": "src/environments/environment.prod.ts"
      }
    ]
  }
}
```

### Adding Custom Environments

To add a `staging` environment:

1. Create `src/environments/environment.staging.ts`
2. Add a `staging` configuration in `angular.json` under `architect.build.configurations`
3. Build with `ng build --configuration=staging`

---

## 7. Deployment Process

### Option 1: Static Hosting (Recommended for SPA)

After running `npm run build`, deploy the contents of `dist/em-app-ui/browser/` to any static host:

- **Netlify / Vercel:** Connect the repository, set the build command to `npm run build` and publish directory to `dist/em-app-ui/browser/`.
- **AWS S3 + CloudFront:** Upload `dist/em-app-ui/browser/` to S3 bucket with static website hosting enabled.
- **GitHub Pages:** Use `ng deploy` with `angular-cli-ghpages`.

**Important:** Configure the hosting provider to redirect all routes to `index.html` for Angular client-side routing to work:

```
/* → /index.html  (200 rewrite)
```

---

### Option 2: Nginx Docker Container

Create `em-app-ui/nginx.conf`:

```nginx
server {
    listen 80;
    server_name _;
    root /usr/share/nginx/html;
    index index.html;

    # Angular routing support
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Backend API proxy (avoids CORS in production)
    location /api/ {
        proxy_pass http://em-app-as:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

Create `em-app-ui/Dockerfile`:

```dockerfile
# Stage 1: Build
FROM node:22-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Stage 2: Serve
FROM nginx:alpine
COPY --from=builder /app/dist/em-app-ui/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

Build and run:

```bash
docker build -t em-app-ui:latest .
docker run -p 80:80 em-app-ui:latest
```

---

### Option 3: Combined `docker-compose` Deployment

Add the following service to the project's `docker-compose.yml`:

```yaml
em-app-ui:
  build: ./em-app-ui
  ports:
    - "80:80"
  depends_on:
    - em-app-as
```

---

### Production Deployment Checklist

- [ ] Update `environment.prod.ts` → `apiUrl` set to production backend URL or `/api` (with proxy)
- [ ] Update OAuth2 redirect URIs in Google/GitHub/Facebook developer consoles to production domain
- [ ] Ensure Nginx (or CDN) rewrites all paths to `index.html`
- [ ] Enable HTTPS on the frontend domain
- [ ] Set `Cache-Control` headers: long cache for hashed JS/CSS, no-cache for `index.html`
- [ ] Configure Content Security Policy (CSP) header
- [ ] Validate bundle size — run `ng build --stats-json` and inspect with `webpack-bundle-analyzer`
- [ ] Disable Angular DevTools in production (enabled by default in development builds only)
- [ ] Verify all API calls use HTTPS (no mixed content)

---

### Bundle Size Analysis

```bash
# Generate stats.json
ng build --configuration=production --stats-json

# Analyze bundle
npx webpack-bundle-analyzer dist/em-app-ui/browser/stats.json
```

Key targets:
- Initial bundle: < 200 KB (gzipped)
- Lazy-loaded `PlatformModule` chunk: < 150 KB (gzipped)
- Total application: < 500 KB (gzipped)

---

### Security Headers (Nginx)

Add to your Nginx configuration for production:

```nginx
add_header X-Frame-Options "SAMEORIGIN" always;
add_header X-Content-Type-Options "nosniff" always;
add_header X-XSS-Protection "1; mode=block" always;
add_header Referrer-Policy "strict-origin-when-cross-origin" always;
add_header Content-Security-Policy "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; font-src 'self' https://fonts.gstatic.com; img-src 'self' data:;" always;
```
