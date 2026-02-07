# UI Application Documentation

## Overview

The em-app-ui is a modern Angular 21 single-page application (SPA) that provides a responsive and intuitive interface for managing users, accounts, and contacts. The application features OAuth2 authentication, Material Design components, and full CRUD functionality.

## Technology Stack

- **Framework**: Angular 21
- **UI Library**: Angular Material 21
- **Language**: TypeScript 5.9
- **Styling**: SCSS
- **State Management**: RxJS
- **HTTP Client**: Angular HttpClient
- **Build Tool**: Angular CLI
- **Testing**: Vitest

## Application Structure

```
src/
├── app/
│   ├── app.ts                      # Root component
│   ├── app-module.ts               # Application module
│   ├── app-routing-module.ts       # Routing configuration
│   ├── core/                       # Core application services
│   │   ├── component/
│   │   │   ├── dashboard/          # Dashboard page
│   │   │   ├── layout/             # Main layout (toolbar + sidenav)
│   │   │   ├── login/              # Login page with OAuth buttons
│   │   │   └── oauth-callback/     # OAuth callback handler
│   │   ├── guards/
│   │   │   └── auth.guard.ts       # Route protection
│   │   ├── interceptors/
│   │   │   ├── jwt.interceptor.ts  # Adds JWT to requests
│   │   │   └── error.interceptor.ts # Global error handling
│   │   ├── model/
│   │   │   └── user.model.ts       # User model
│   │   └── services/
│   │       ├── auth.service.ts     # Authentication service
│   │       └── session-storage.service.ts
│   ├── features/                   # Feature modules
│   │   ├── component/platform/
│   │   │   ├── users/             # User management
│   │   │   │   ├── users.component.ts
│   │   │   │   ├── users.component.html
│   │   │   │   ├── users.component.scss
│   │   │   │   └── user-dialog/   # User CRUD dialog
│   │   │   ├── accounts/          # Account management
│   │   │   │   ├── accounts.component.ts
│   │   │   │   ├── accounts.component.html
│   │   │   │   ├── accounts.component.scss
│   │   │   │   └── account-dialog/ # Account CRUD dialog
│   │   │   └── contacts/          # Contact management
│   │   │       ├── contacts.component.ts
│   │   │       ├── contacts.component.html
│   │   │       ├── contacts.component.scss
│   │   │       └── contact-dialog/ # Contact CRUD dialog
│   │   ├── models/
│   │   │   ├── api.platform.model.ts  # Platform entity models
│   │   │   ├── api.shared.model.ts    # Shared API models
│   │   │   └── api.base.model.ts      # Base entity models
│   │   └── service/platform/
│   │       ├── user.service.ts     # User API service
│   │       ├── account.service.ts  # Account API service
│   │       └── contact.service.ts  # Contact API service
│   └── shared/                     # Shared components
│       └── components/
│           └── confirm-dialog/     # Confirmation dialog
├── environments/
│   └── environment.ts             # Environment configuration
├── index.html                     # HTML entry point
├── main.ts                        # Application bootstrap
└── styles.scss                    # Global styles
```

## Key Features

### 1. Authentication

#### OAuth2 Login
The application supports multiple OAuth2 providers:
- Google
- GitHub
- Facebook
- TikTok

**Login Flow**:
1. User navigates to `/login`
2. User selects OAuth provider
3. Application redirects to backend OAuth endpoint
4. Backend handles OAuth flow with provider
5. Backend redirects to `/oauth/callback?token={jwt}`
6. Frontend stores JWT and redirects to dashboard

**Components**:
- `LoginComponent`: Displays OAuth provider buttons
- `OauthCallbackComponent`: Handles OAuth callback and token storage
- `AuthService`: Manages authentication state and token storage

#### Route Protection
Protected routes require authentication via `AuthGuard`:
- `/dashboard`
- `/users`
- `/accounts`
- `/contacts`

Unauthenticated users are redirected to `/login`.

### 2. User Management

**Component**: `UsersComponent`  
**Path**: `/users`

#### Features
- Display all users in a grid layout
- Create new users
- Edit existing users
- Delete users with confirmation
- View user details (name, email, provider, avatar)
- Loading and error states

#### User Dialog
**Component**: `UserDialogComponent`

**Fields**:
- Username (required)
- Email (required, validated)
- Full Name (required)
- Status (required: ACTIVE, INACTIVE, PENDING)
- First Name (required)
- Last Name (required)
- Phone (optional)
- Company (optional)

**Validation**:
- Required field validation
- Email format validation
- Real-time error messages

#### Operations

**Create User**:
```typescript
// Click "Add User" button
createUser(): void {
  const dialogRef = this.dialog.open(UserDialogComponent, {
    width: '600px',
    data: { mode: 'create' }
  });
  
  dialogRef.afterClosed().subscribe(result => {
    if (result) {
      this.userService.createUser(result).subscribe({
        next: () => {
          this.snackBar.open('User created successfully', 'Close');
          this.loadUsers();
        }
      });
    }
  });
}
```

**Edit User**:
```typescript
// Click "Edit" button on user card
editUser(user: User): void {
  const dialogRef = this.dialog.open(UserDialogComponent, {
    width: '600px',
    data: { user, mode: 'edit' }
  });
  
  dialogRef.afterClosed().subscribe(result => {
    if (result && result.id) {
      this.userService.updateUser(result.id, result).subscribe({
        next: () => {
          this.snackBar.open('User updated successfully', 'Close');
          this.loadUsers();
        }
      });
    }
  });
}
```

**Delete User**:
```typescript
// Click "Delete" button on user card
deleteUser(user: User): void {
  const dialogRef = this.dialog.open(ConfirmDialogComponent, {
    width: '400px',
    data: {
      title: 'Delete User',
      message: `Are you sure you want to delete user "${user.name}"?`
    }
  });
  
  dialogRef.afterClosed().subscribe(confirmed => {
    if (confirmed && user.id) {
      this.userService.deleteUser(user.id).subscribe({
        next: () => {
          this.snackBar.open('User deleted successfully', 'Close');
          this.loadUsers();
        }
      });
    }
  });
}
```

### 3. Account Management

**Component**: `AccountsComponent`  
**Path**: `/accounts`

#### Features
- Display all accounts in a grid layout
- Create new accounts
- Edit existing accounts
- Delete accounts with confirmation
- View account details (name, type, status, created date)

#### Account Dialog
**Component**: `AccountDialogComponent`

**Fields**:
- Account Name (required)
- Account Type (required: BUSINESS, PERSONAL, GOVERNMENT, NON_PROFIT)
- Status (required: ACTIVE, INACTIVE, PENDING)

#### Operations
Similar to User Management, with Account-specific fields and validations.

### 4. Contact Management

**Component**: `ContactsComponent`  
**Path**: `/contacts`

#### Features
- Display all contacts in a grid layout
- Create new contacts
- Edit existing contacts
- Delete contacts with confirmation
- View contact details (name, email, phone, company)

#### Contact Dialog
**Component**: `ContactDialogComponent`

**Fields**:
- First Name (required)
- Last Name (required)
- Email (required, validated)
- Phone (optional)
- Company (optional)

#### Operations
Similar to User Management, with Contact-specific fields and validations.

### 5. Shared Components

#### Confirm Dialog
**Component**: `ConfirmDialogComponent`

A reusable confirmation dialog for destructive operations (e.g., delete).

**Props**:
- `title`: Dialog title
- `message`: Confirmation message
- `confirmText`: Confirm button text (default: "Confirm")
- `cancelText`: Cancel button text (default: "Cancel")

**Returns**: `boolean` - true if confirmed, false if canceled

### 6. Services

#### AuthService
Manages authentication state and operations.

**Methods**:
- `login(provider: string)`: Initiates OAuth login
- `logout()`: Clears token and redirects to login
- `isAuthenticated()`: Checks if user is authenticated
- `getCurrentUser()`: Returns current user observable
- `handleOAuthCallback(token: string)`: Processes OAuth callback

#### UserService
Handles User API operations.

**Methods**:
- `getUsers()`: Get all users
- `getUser(id: number)`: Get user by ID
- `createUser(user: User)`: Create new user
- `updateUser(id: number, user: User)`: Update user
- `deleteUser(id: number)`: Delete user

#### AccountService
Handles Account API operations (similar to UserService).

#### ContactService
Handles Contact API operations (similar to UserService).

### 7. HTTP Interceptors

#### JwtInterceptor
Automatically adds JWT token to all HTTP requests.

```typescript
intercept(request: HttpRequest<any>, next: HttpHandler) {
  const token = localStorage.getItem('jwt_token');
  if (token) {
    request = request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }
  return next.handle(request);
}
```

#### ErrorInterceptor
Handles HTTP errors globally, especially 401 Unauthorized.

```typescript
intercept(request: HttpRequest<any>, next: HttpHandler) {
  return next.handle(request).pipe(
    catchError(error => {
      if (error.status === 401) {
        this.authService.logout();
      }
      return throwError(() => error);
    })
  );
}
```

## Styling

### Theme
- Primary Color: Material Blue
- Accent Color: Material Pink
- Warn Color: Material Red

### Layout
- Responsive design using CSS Grid and Flexbox
- Mobile-first approach
- Material Design elevation and shadows
- Consistent spacing using Material Design guidelines

### Components
- Cards for entity display
- Dialogs for CRUD operations
- Snackbar for notifications
- Progress spinner for loading states
- Material Icons throughout

## Running the Application

### Development Server
```bash
cd em-app-ui
npm install
npm start
```
Application runs at: `http://localhost:4200`

### Build for Production
```bash
npm run build
```
Output: `dist/em-app-ui/`

### Build for Development
```bash
npm run build -- --configuration=development
```

### Run Tests
```bash
npm test
```

## Environment Configuration

### Development (`environment.ts`)
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'
};
```

### Production (`environment.prod.ts`)
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://api.production.com'
};
```

## Best Practices

### State Management
- Use RxJS observables for reactive state
- Unsubscribe from observables in `ngOnDestroy()`
- Use async pipe in templates when possible

### Error Handling
- Display user-friendly error messages
- Log detailed errors to console
- Provide retry options for failed operations

### Performance
- Lazy load feature modules when possible
- Use OnPush change detection strategy
- Minimize bundle size
- Use trackBy for *ngFor loops

### Security
- Store JWT in localStorage (consider httpOnly cookies for production)
- Validate user input in forms
- Sanitize user-generated content
- Use HTTPS in production

## Troubleshooting

### Common Issues

**Issue**: Build fails with font loading error  
**Solution**: Use development configuration which skips font inlining
```bash
npm run build -- --configuration=development
```

**Issue**: API calls return 401 Unauthorized  
**Solution**: Check if JWT token is valid and not expired. Re-authenticate if necessary.

**Issue**: CORS errors in browser  
**Solution**: Ensure backend CORS configuration allows `http://localhost:4200`

**Issue**: Components not loading  
**Solution**: Check that all components are declared in `app-module.ts`

## Future Enhancements

- Implement pagination for large datasets
- Add advanced search and filtering UI
- Implement bulk operations (select multiple, bulk delete)
- Add user profile page with avatar upload
- Implement real-time updates using WebSockets
- Add dark mode theme
- Implement offline support with service workers
- Add accessibility (ARIA labels, keyboard navigation)
- Implement unit and e2e tests
