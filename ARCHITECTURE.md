# System Architecture Documentation

## Overview

The Elite Maintenance Application (em-app) is a modern, enterprise-grade full-stack application built with Angular 21 and Spring Boot 4.0.1. It implements a clean three-tier architecture with OAuth2 authentication and JWT-based authorization.

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         User's Browser                               │
│  ┌────────────────────────────────────────────────────────────────┐ │
│  │              Angular 21 SPA (Port 4200)                         │ │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐        │ │
│  │  │   Login      │  │  Dashboard   │  │   Users/     │        │ │
│  │  │   Component  │  │  Component   │  │   Accounts/  │        │ │
│  │  │              │  │              │  │   Contacts   │        │ │
│  │  └──────────────┘  └──────────────┘  └──────────────┘        │ │
│  │                                                                 │ │
│  │  ┌────────────────────────────────────────────────────────┐   │ │
│  │  │  Services: Auth, User, Account, Contact                │   │ │
│  │  └────────────────────────────────────────────────────────┘   │ │
│  │  ┌────────────────────────────────────────────────────────┐   │ │
│  │  │  Interceptors: JWT, Error Handling                     │   │ │
│  │  └────────────────────────────────────────────────────────┘   │ │
│  └────────────────────────────────────────────────────────────────┘ │
└────────────────────────┬────────────────────────────────────────────┘
                         │ HTTPS/REST API + JWT Bearer Token
                         │
┌────────────────────────▼─────────────────────────────────────────────┐
│              Spring Boot Backend (Port 8080)                          │
│  ┌─────────────────────────────────────────────────────────────────┐ │
│  │                    Security Layer                                │ │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │ │
│  │  │ JWT Filter   │→ │OAuth2 Config │→ │ CORS Config  │         │ │
│  │  └──────────────┘  └──────────────┘  └──────────────┘         │ │
│  └─────────────────────────────────────────────────────────────────┘ │
│  ┌─────────────────────────────────────────────────────────────────┐ │
│  │                    REST Controllers Layer                        │ │
│  │  UserController, AccountController, ContactController            │ │
│  │  /api/v1/platform/user, /account, /contact                      │ │
│  │  /auth/user, /auth/status, /oauth2/**                           │ │
│  └─────────────────────────────────────────────────────────────────┘ │
│  ┌─────────────────────────────────────────────────────────────────┐ │
│  │                    Service Layer                                 │ │
│  │  UserService, AccountService, ContactService                     │ │
│  │  (Business logic, validations, transactions)                     │ │
│  └─────────────────────────────────────────────────────────────────┘ │
│  ┌─────────────────────────────────────────────────────────────────┐ │
│  │                    DAO Layer                                     │ │
│  │  UserDao, AccountDao, ContactDao                                 │ │
│  │  (Data access, filtering, pagination)                            │ │
│  └─────────────────────────────────────────────────────────────────┘ │
│  ┌─────────────────────────────────────────────────────────────────┐ │
│  │                    Repository Layer                              │ │
│  │  Spring Data JPA Repositories                                    │ │
│  └─────────────────────────────────────────────────────────────────┘ │
└────────────────────────┬─────────────────────────────────────────────┘
                         │ JDBC (MySQL Connector)
                         │
┌────────────────────────▼─────────────────────────────────────────────┐
│                      MySQL Database (Port 3306)                       │
│  ┌─────────────────────────────────────────────────────────────────┐ │
│  │  Tables: PLATFORM_USER, PLATFORM_ACCOUNT, PLATFORM_CONTACT,     │ │
│  │          PLATFORM_CONTACT_EMAIL, PLATFORM_CONTACT_PHONE,        │ │
│  │          PLATFORM_CONTACT_ADDRESS, PLATFORM_ACCOUNT_CONTACT     │ │
│  └─────────────────────────────────────────────────────────────────┘ │
└───────────────────────────────────────────────────────────────────────┘

External OAuth2 Providers (redirects during login flow):
┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐
│  Google  │  │  GitHub  │  │ Facebook │  │  TikTok  │
└────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘
     │             │              │              │
     └─────────────┴──────────────┴──────────────┘
                   │
                   ▼ OAuth2 Authorization Code Flow
         Spring Security OAuth2 Client
```

## Module Architecture

### em-app-as (Application Server)
The Spring Boot backend module that handles all API requests and business logic.

**Package Structure**:
```
ca.bigmwaj.emapp.as/
├── api/                    # REST Controllers
│   ├── platform/           # Domain controllers
│   │   ├── UserController.java
│   │   ├── AccountController.java
│   │   └── ContactController.java
│   └── shared/             # Shared API components
├── config/                 # Configuration classes
│   ├── SecurityConfig.java
│   ├── JwtConfig.java
│   └── SwaggerConfig.java
├── dao/                    # Data Access Objects
│   └── platform/
│       ├── UserDao.java
│       ├── AccountDao.java
│       └── ContactDao.java
├── dto/                    # Data Transfer Objects
│   ├── platform/
│   │   ├── UserDto.java
│   │   ├── AccountDto.java
│   │   └── ContactDto.java
│   └── shared/
├── service/                # Business Logic Services
│   └── platform/
│       ├── UserService.java
│       ├── AccountService.java
│       └── ContactService.java
├── security/               # Security components
│   ├── JwtTokenProvider.java
│   └── JwtAuthenticationFilter.java
└── exception/              # Exception handlers
    └── GlobalExceptionHandler.java
```

**Key Components**:
- **Controllers**: Handle HTTP requests, validate input, return responses
- **Services**: Implement business logic with @Transactional boundaries
- **DAOs**: Execute database queries with filtering/sorting/pagination
- **DTOs**: API contract objects (separated from entities)
- **MapStruct**: Automatic DTO ↔ Entity mapping

### em-app-dm (Data Model)
Shared module containing JPA entities and enums used across the application.

**Package Structure**:
```
ca.bigmwaj.emapp.dm/
├── entity/                 # JPA Entities
│   └── platform/
│       ├── UserEntity.java
│       ├── AccountEntity.java
│       ├── ContactEntity.java
│       └── ...
├── lvo/                    # List Value Objects (Enums)
│   └── platform/
│       ├── UserStatusLvo.java
│       ├── AccountStatusLvo.java
│       └── ...
└── shared/                 # Base entities
    └── BaseHistEntity.java
```

**Database Schema**:
```
PLATFORM_USER (id, username, password, contact_id, status, audit_fields)
    ↓ 1:1
PLATFORM_CONTACT (id, first_name, last_name, birth_date, audit_fields)
    ↓ 1:N
    ├─→ PLATFORM_CONTACT_EMAIL (contact_id, email_address, email_type)
    ├─→ PLATFORM_CONTACT_PHONE (contact_id, phone_number, phone_type)
    └─→ PLATFORM_CONTACT_ADDRESS (contact_id, street, city, province, postal_code)

PLATFORM_ACCOUNT (id, name, description, status, audit_fields)
    ↓ M:N
PLATFORM_ACCOUNT_CONTACT (account_id, contact_id, role)
```

### em-app-ui (Angular Frontend)
Modern Angular 21 single-page application with Material Design.

**Directory Structure**:
```
src/app/
├── core/                   # Core module (singleton services)
│   ├── component/
│   │   ├── login/         # Login page with OAuth providers
│   │   ├── dashboard/     # Main dashboard
│   │   ├── layout/        # App shell (toolbar, sidebar)
│   │   └── oauth-callback/# OAuth redirect handler
│   ├── services/
│   │   ├── auth.service.ts           # Authentication logic
│   │   └── session-storage.service.ts # Token persistence
│   ├── guards/
│   │   └── auth.guard.ts  # Route protection
│   └── interceptors/
│       ├── jwt.interceptor.ts        # Add JWT to requests
│       └── error.interceptor.ts      # Handle 401 errors
├── features/               # Feature modules
│   ├── component/
│   │   └── platform/
│   │       ├── users/     # User management component
│   │       ├── accounts/  # Account management component
│   │       └── contacts/  # Contact management component
│   ├── service/
│   │   └── platform/
│   │       ├── user.service.ts
│   │       ├── account.service.ts
│   │       └── contact.service.ts
│   └── models/
│       ├── api.platform.model.ts     # User, Account, Contact interfaces
│       └── api.shared.model.ts       # SearchResult, etc.
├── app-routing-module.ts   # Route definitions
└── app.ts                  # Root component
```

## Authentication Flow

### OAuth2 Authorization Code Flow

```
1. User clicks "Login with Google" on Angular login page
   │
   ▼
2. Angular redirects to: /oauth2/authorization/google (Spring Boot)
   │
   ▼
3. Spring Security redirects to Google's authorization URL
   │
   ▼
4. User authenticates with Google and grants permissions
   │
   ▼
5. Google redirects back to: /login/oauth2/code/google?code=ABC123
   │
   ▼
6. Spring Security OAuth2 Client exchanges code for access token
   │
   ▼
7. Backend retrieves user info from Google's /userinfo endpoint
   │
   ▼
8. Backend generates JWT token (HMAC-SHA256, 24h expiry)
   │
   ▼
9. Backend redirects to: http://localhost:4200/oauth-callback?token=JWT_TOKEN
   │
   ▼
10. Angular OAuthCallbackComponent:
    - Extracts token from URL
    - Stores in localStorage
    - Redirects to /dashboard
```

### API Request Flow with JWT

```
1. User navigates to /users in Angular
   │
   ▼
2. UsersComponent calls UserService.getUsers()
   │
   ▼
3. HttpClient makes GET request to /api/v1/platform/user
   │
   ▼
4. JwtInterceptor adds header: Authorization: Bearer {token}
   │
   ▼
5. Spring Security JwtAuthenticationFilter validates token:
   - Extracts token from Authorization header
   - Verifies signature using secret key
   - Checks expiration
   - Creates Authentication object
   │
   ▼
6. If valid: Request proceeds to UserController
   If invalid: Returns 401 Unauthorized
   │
   ▼
7. Controller → Service → DAO → Database
   │
   ▼
8. Response flows back with data
   │
   ▼
9. Angular displays users in Material cards
```

### Error Handling Flow

```
API Request fails with 401 Unauthorized
   │
   ▼
ErrorInterceptor catches the error
   │
   ▼
Checks if user is authenticated
   │
   ├─ Yes: Token expired
   │  └─→ Clear localStorage
   │  └─→ Redirect to /login
   │  └─→ Show "Session expired" message
   │
   └─ No: Already logged out
      └─→ Redirect to /login
```

## Data Flow Patterns

### Create/Update Pattern

```
Angular Component
   │ User fills form
   ▼
User clicks "Save"
   │
   ▼
Component calls service.createUser(userData)
   │
   ▼
HTTP POST /api/v1/platform/user
   │ (with JWT token)
   ▼
UserController.create(@RequestBody UserDto)
   │ @Validated annotation triggers validation
   ▼
UserService.create(dto)
   │ @Transactional starts transaction
   ▼
Convert DTO → Entity (MapStruct)
   │
   ▼
UserDao.save(entity)
   │
   ▼
JPA Repository persists to database
   │ Transaction commits
   ▼
Return saved entity → DTO → HTTP 200 OK
   │
   ▼
Angular updates UI with new data
```

### Search/Filter Pattern

```
User enters search criteria
   │
   ▼
Component builds FilterBy and SortBy parameters
   │
   ▼
HTTP GET /api/v1/platform/user?filters=...&sortBy=...&pageSize=20
   │
   ▼
Custom @ValidFilterByPatterns validates filter fields
   │
   ▼
UserController.search(pageSize, pageIndex, filters, sortBy)
   │
   ▼
UserService.search(filterDto)
   │
   ▼
UserDao builds dynamic JPA query:
   - Adds WHERE clauses for filters
   - Adds ORDER BY for sorting
   - Adds LIMIT/OFFSET for pagination
   │
   ▼
Execute query and count query (if calculateStatTotal=true)
   │
   ▼
Return SearchResultDto<UserDto>:
   - data: List<UserDto>
   - totalElements: Long
   - pageSize: Integer
   - pageIndex: Integer
```

## Security Architecture

### JWT Token Structure

```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "user@example.com",
    "name": "John Doe",
    "iat": 1706000000,
    "exp": 1706086400
  },
  "signature": "HMAC-SHA256(header + payload + secret)"
}
```

**Token Configuration**:
- Algorithm: HMAC-SHA256
- Key Size: 256+ bits
- Expiration: 86400000ms (24 hours)
- Storage: localStorage (⚠️ vulnerable to XSS, use httpOnly cookies in production)

### CORS Configuration

```yaml
Allowed Origins:
  - http://localhost:4200 (Angular dev server)
  - http://localhost:3000 (alternate port)

Allowed Methods:
  - GET, POST, PUT, DELETE, PATCH, OPTIONS

Allowed Headers:
  - Authorization, Content-Type, Accept

Max Age: 3600 seconds
```

### Protected vs Public Endpoints

**Public** (no authentication required):
```
/, /error, /favicon.ico
/auth/**
/oauth2/**
/login, /logout
/swagger-ui/**
/v3/api-docs/**
/actuator/health
```

**Protected** (JWT required):
```
/api/v1/**
/auth/user (GET current user)
```

## Technology Choices & Rationale

### Frontend: Angular 21 + Material Design
**Why Angular?**
- Strong TypeScript support with strict mode
- Dependency injection for testability
- RxJS for reactive programming
- Large enterprise ecosystem

**Why Material Design?**
- Google's design system (consistent UX)
- Comprehensive component library (cards, dialogs, buttons, forms)
- Built-in accessibility (ARIA labels)
- Responsive layout utilities

### Backend: Spring Boot 4.0.1 + Spring Security
**Why Spring Boot?**
- Production-ready framework
- Auto-configuration reduces boilerplate
- Built-in security, transactions, validation
- Large community and ecosystem

**Why Spring Security + OAuth2?**
- Industry-standard OAuth2 implementation
- Built-in protection against common attacks
- Flexible authentication/authorization
- JWT token support

### Database: MySQL 8.1+
**Why MySQL?**
- Proven reliability for transactional data
- Good performance for read-heavy workloads
- Wide hosting support
- Strong ACID compliance

### Additional Libraries
- **MapStruct**: Type-safe DTO mapping (better than manual or reflection-based)
- **JJWT**: JWT creation/validation (actively maintained)
- **Lombok**: Reduce boilerplate (getters/setters)
- **SpringDoc**: OpenAPI documentation generation

## Scalability Considerations

### Current Architecture (Single Server)
- Suitable for: < 1000 concurrent users
- Stateless JWT enables horizontal scaling
- Database is the bottleneck

### Horizontal Scaling Strategy
```
Load Balancer
   ↓
Spring Boot Instance 1 ─┐
Spring Boot Instance 2 ─┼─→ MySQL Primary
Spring Boot Instance 3 ─┘       ↓
                                MySQL Read Replicas
```

**Requirements for horizontal scaling**:
1. ✅ Stateless authentication (JWT) - Already implemented
2. ❌ Externalize session storage - Not needed (stateless)
3. ❌ Distributed caching - Add Redis for frequently accessed data
4. ❌ Database replication - MySQL primary/replica setup
5. ❌ File storage - Use S3/blob storage instead of local filesystem

### Performance Optimization Opportunities
1. **Caching Layer**: Add Spring Cache + Redis for user/account lookups
2. **Database Indexing**: Index frequently queried columns (username, email, status)
3. **Connection Pooling**: Configure HikariCP for optimal connection reuse
4. **Query Optimization**: Use JPA @EntityGraph to avoid N+1 queries
5. **API Pagination**: Already implemented, ensure proper usage
6. **CDN**: Serve static Angular assets from CDN in production

## Deployment Architecture

### Local Development
```
Developer Machine
├── MySQL (Docker)
├── Spring Boot (mvn spring-boot:run)
└── Angular (ng serve)
```

### Production (Recommended)
```
┌─────────────────────────────────────────────┐
│              Cloud Provider                  │
│  ┌─────────────────────────────────────┐   │
│  │  CDN (CloudFront/CloudFlare)        │   │
│  │  └─→ Angular static files           │   │
│  └─────────────────────────────────────┘   │
│  ┌─────────────────────────────────────┐   │
│  │  Load Balancer (ALB/NGINX)          │   │
│  │  └─→ SSL Termination                │   │
│  └─────────────────────────────────────┘   │
│  ┌─────────────────────────────────────┐   │
│  │  Spring Boot Instances              │   │
│  │  - Auto-scaling group (2-10 nodes)  │   │
│  │  - Health checks enabled            │   │
│  └─────────────────────────────────────┘   │
│  ┌─────────────────────────────────────┐   │
│  │  RDS MySQL (Primary + Read Replica) │   │
│  │  - Automated backups                │   │
│  │  - Multi-AZ deployment              │   │
│  └─────────────────────────────────────┘   │
│  ┌─────────────────────────────────────┐   │
│  │  Redis (ElastiCache)                │   │
│  │  - Session caching                  │   │
│  │  - API response caching             │   │
│  └─────────────────────────────────────┘   │
└─────────────────────────────────────────────┘
```

## Design Patterns Used

1. **Layered Architecture**: Controller → Service → DAO → Repository
2. **DTO Pattern**: Separate API contracts from domain entities
3. **Dependency Injection**: Constructor-based DI throughout
4. **Repository Pattern**: Spring Data JPA repositories
5. **Interceptor Pattern**: JWT and error interceptors
6. **Guard Pattern**: Route guards for authentication
7. **Observer Pattern**: RxJS Observables for async operations
8. **Factory Pattern**: MapStruct mappers
9. **Singleton Pattern**: Angular services (providedIn: 'root')
10. **Strategy Pattern**: OAuth2 provider selection

## Future Enhancements

### Security
- [ ] Implement BCrypt password hashing
- [ ] Add refresh token mechanism
- [ ] Use httpOnly cookies for JWT storage
- [ ] Implement rate limiting
- [ ] Add role-based access control (RBAC)

### Features
- [ ] Real-time notifications (WebSocket)
- [ ] File upload/download
- [ ] Advanced search with Elasticsearch
- [ ] Audit logging
- [ ] Multi-tenancy support

### DevOps
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Automated testing (unit + integration)
- [ ] Container orchestration (Kubernetes)
- [ ] Monitoring (Prometheus + Grafana)
- [ ] Centralized logging (ELK stack)

---

**Document Version**: 2.0  
**Last Updated**: February 2026  
**Maintained By**: Development Team
