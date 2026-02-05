# EM App - Angular Material Web Client

This is an Angular 16+ web client with OAuth2 authentication for the EM App REST backend.

## Features

- **Angular 16+** with TypeScript
- **Angular Material** for modern UI components
- **OAuth2 Authentication** with multiple providers:
  - Google
  - GitHub
  - Facebook
  - TikTok
- **JWT Token Management** with HTTP Interceptors
- **Route Guards** for protected pages
- **RxJS** for reactive state management
- **Clean Architecture** with modular structure

## Architecture

```
src/app/
├── core/                    # Core functionality (singleton services)
│   ├── guards/             # Route guards (AuthGuard)
│   ├── interceptors/       # HTTP interceptors (JWT, Error)
│   ├── models/             # Data models and interfaces
│   └── services/           # Core services (AuthService)
├── features/               # Feature modules
│   ├── login/             # Login page with OAuth buttons
│   ├── oauth-callback/    # OAuth callback handler
│   └── dashboard/         # Main dashboard
├── shared/                # Shared components and utilities
│   └── components/
│       └── layout/        # Main layout with toolbar and sidenav
└── environments/          # Environment configurations
```

## Prerequisites

- Node.js 16+ and npm
- Angular CLI 16+
- Backend server running on `http://localhost:8080`

## Installation

1. Install dependencies:
```bash
npm install
```

2. Configure environment variables (optional):
Edit `src/environments/environment.ts` to change the API URL.

## Development Server

Run the development server:
```bash
ng serve
```

Navigate to `http://localhost:4200/`.

## Authentication Flow

1. User visits the app → Redirected to login page (if not authenticated)
2. User clicks OAuth provider button → Redirected to backend OAuth2 endpoint
3. Backend handles OAuth flow → Redirects to provider's login page
4. User authenticates with provider → Provider redirects back to backend
5. Backend generates JWT token → Redirects to frontend callback with token
6. Frontend stores token → User can access protected routes

## Building for Production

Build the project:
```bash
ng build --configuration production
```

The build artifacts will be stored in the `dist/` directory.
