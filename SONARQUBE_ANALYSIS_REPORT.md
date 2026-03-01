# SonarQube-Style Static Analysis Report

**Project:** Elite Maintenance Application (em-app)  
**Scope:** Full-stack — `em-app-as` (Spring Boot) + `em-app-ui` (Angular)  
**Analysis Date:** 2026-03-01  
**Analyst Role:** Senior Software Architect & Code Quality Expert  

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Code Quality Analysis](#2-code-quality-analysis)
3. [Security Analysis](#3-security-analysis)
4. [Performance Analysis](#4-performance-analysis)
5. [Architecture Review](#5-architecture-review)
6. [Testing Quality](#6-testing-quality)
7. [Recommendation Document](#7-recommendation-document)

---

## 1. Executive Summary

### Overall Project Health Score: **C**

> **Rating rationale (SonarQube A–E scale):**  
> The codebase demonstrates a solid architectural foundation with proper layering, good use of abstraction, and modern tooling. However, it contains several **critical security vulnerabilities** (hardcoded credentials in tracked files, hardcoded Authorization header) and notable code-quality issues (double save, SRP violations, misnamed classes) that lower the score from B to C. Resolving the critical security issues would elevate this to a **B**.

---

### Main Strengths

| Strength | Detail |
|----------|--------|
| **Layered Architecture** | Clean API → Service → DAO → Entity separation throughout `em-app-as` |
| **Abstract Base Classes** | `AbstractMainService`, `AbstractDao`, `AbstractBaseAPI`, `AbstractBaseEntity` reduce duplication |
| **MapStruct DTO Mapping** | Compile-time mapping with `GlobalPlatformMapper` avoids runtime reflection overhead |
| **XML-Based Validation Engine** | Custom `ValidationXmlParser` + `RuleFactory` provides a configurable, reusable rules system |
| **N+1 Query Prevention** | `@Fetch(FetchMode.SUBSELECT)` used consistently on collection relationships |
| **Dual Authentication** | OAuth2 (Google, GitHub, Facebook) + username/password JWT flow both supported |
| **Comprehensive Test Suite** | 22+ test classes including unit and integration tests with builder pattern for fixtures |
| **Angular Modular Architecture** | Core / Features / Shared modules well-separated; HTTP interceptors for cross-cutting concerns |
| **Advanced Search API** | Dynamic `WhereClause` / `SortByClause` with annotation-driven field validation |

---

### Critical Risks

| Risk | Severity | File |
|------|----------|------|
| OAuth2 client secrets committed to source | 🔴 CRITICAL | `em-app-as/src/main/resources/application.yml` |
| Hardcoded Basic-Auth credentials in Camel route | 🔴 CRITICAL | `em-app-ig/.../DeadLetterSenderRoute.java` |
| JWT stored in `localStorage` (XSS-accessible) | 🔴 HIGH | `em-app-ui/.../session-storage.service.ts` |
| Weak default JWT secret in config | 🔴 HIGH | `em-app-as/src/main/resources/application.yml` |
| `show-sql: true` leaks SQL in production logs | 🟠 HIGH | `em-app-as/src/main/resources/application.yml` |
| Double entity save in `UserService.create()` | 🟠 HIGH | `em-app-as/.../service/platform/UserService.java` |
| `UserService` implements `AuthenticationManager` (SRP violation) | 🟡 MEDIUM | `em-app-as/.../service/platform/UserService.java` |

---

### Technical Debt Estimation

| Category | Estimate |
|----------|----------|
| **Security remediation** | 2–3 days (immediate) |
| **Code smell resolution** | 3–5 days (short-term) |
| **Architecture refactoring** | 5–10 days (medium-term) |
| **Test coverage improvement** | 5–8 days (ongoing) |
| **Total estimated debt** | ~15–26 developer-days |

---

## 2. Code Quality Analysis

### 2.1 Backend (`em-app-as`)

#### Code Smells

| ID | Severity | Location | Description |
|----|----------|----------|-------------|
| CS-001 | 🔴 Major | `UserService.java:55–56` | **Double save**: `dao.save(entity)` called twice consecutively. Second save returns the same persisted entity; the first save result is discarded. |
| CS-002 | 🟡 Minor | `UserService.java:43` | `PasswordEncoder` instantiated with `new BCryptPasswordEncoder()` instead of injected via Spring. Bypasses Spring IoC and makes testing harder. |
| CS-003 | 🟡 Minor | `AbstractMainService.java:23` | `protected static final String SYSTEM_USER = "IA"` declared but never used in the visible codebase. Dead constant. |
| CS-004 | 🟡 Minor | `GlobalExceptionHandler.java` | Error messages are in French (`"Une erreur est survenue"`). The rest of the codebase (code, comments, Swagger docs) is in English. Language inconsistency. |
| CS-005 | 🟡 Minor | `SecurityConfig.java:97–110` | Large commented-out code block (two full `@Bean` methods for `AuthenticationManager`). Committed dead code should be removed. |
| CS-006 | 🟡 Minor | `EmailSenderRoute.java:7` | `@Component` annotation commented out. Route class exists but is permanently disabled. Should be removed or documented as intentionally disabled with a tracking ticket reference. |
| CS-007 | 🟡 Minor | `UserController.java` | Method `search()` has 10 parameters. Extract a dedicated `UserSearchRequest` wrapper object. |
| CS-008 | 🔵 Info | Multiple services | Services use `@Autowired` on fields instead of constructor injection. Field injection hides dependencies and makes unit testing harder. |
| CS-009 | 🔵 Info | `AbstractMainService.java` | `searchAll()` method loads the entire table without pagination. Dangerous for large datasets. |

#### Duplications

| ID | Location | Description |
|----|----------|-------------|
| DUP-001 | `UserService`, `AccountService`, `ContactService` | Password-encoding logic pattern (`if dto.getPassword() != null && !dto.getPassword().isEmpty()`) repeated in `create()` and `update()`. Extract to a private helper or utility. |
| DUP-002 | All controllers | `@Positive @PathVariable Short id` repeated in every controller's `findById` and `delete` methods. Consider base controller or consistent parameter object. |
| DUP-003 | `UserDao`, `AccountDao`, `ContactDao` | `getEntityClass()` method is implemented identically in every DAO with only the class literal changing. A generic default in `AbstractDao` could eliminate this. |

#### Complexity

| ID | Location | Cyclomatic Complexity | Action |
|----|----------|-----------------------|--------|
| CC-001 | `ValidationXmlParser.java` | Estimated 8–12 | XML parsing with multiple conditional paths. Acceptable for dedicated parser; ensure unit tests cover all branches. |
| CC-002 | `UserController.search()` | 1 (low) but 10 parameters | Complexity is in parameter count, not logic. Refactor to a request object. |
| CC-003 | `AbstractDao.findAllByCriteria()` | Moderate | Dynamic query building has multiple conditional paths — adequately tested by `WhereClausePatternsConverterTest`. |

#### Naming Conventions

| ID | Location | Issue |
|----|----------|-------|
| NC-001 | `SessionStorageService.ts` | Class is named `SessionStorageService` but internally uses `window.localStorage`. Misleading — should be `LocalStorageService`. |
| NC-002 | `UserService.java:authMngr` | In `SecurityConfig.java`, the `UserService` bean is injected as `authMngr`. Using a service class as an auth manager and naming it `authMngr` obscures intent. |
| NC-003 | `AbstractMainService.java` | `SYSTEM_USER = "IA"` — the constant name `SYSTEM_USER` refers to "IA" (Intelligence Artificielle). Opaque naming; `AUDIT_USER_AI` would be clearer. |
| NC-004 | `UserEntity.java` | ID field is of type `Short`. Using `Short` for a primary key is unconventional and limits the table to 32,767 rows before overflow. |

#### SOLID Principle Violations

| ID | Principle | Location | Description |
|----|-----------|----------|-------------|
| SOLID-001 | **Single Responsibility** | `UserService.java` | `UserService` is a business-logic service *and* an `AuthenticationManager`. Authentication concerns should live in a dedicated class. |
| SOLID-002 | **Dependency Inversion** | `UserService.java:43` | `new BCryptPasswordEncoder()` is a direct dependency on a concrete class rather than the `PasswordEncoder` interface injected by Spring. |
| SOLID-003 | **Open/Closed** | `GlobalPlatformMapper.java` | All platform entity ↔ DTO mappings are consolidated in a single mapper. Adding new entities requires modifying this central mapper class. |

#### Layering Issues

| ID | Location | Description |
|----|----------|-------------|
| LY-001 | `SecurityConfig.java` | Directly autowires `UserService` as `AuthenticationManager`. Security configuration should depend on an interface, not a concrete service. |
| LY-002 | `DeadLetterSenderRoute.java` | Apache Camel route makes direct REST calls to `localhost:8080` — tightly couples the integration gateway to the application server's host/port. |

#### Dead Code

| ID | Location | Description |
|----|----------|-------------|
| DC-001 | `EmailSenderRoute.java` | Class exists with `@Component` commented out. Entire class is dead. |
| DC-002 | `SecurityConfig.java:97–110` | Two `@Bean` methods for `AuthenticationManager` are commented out. |
| DC-003 | `AbstractMainService.java:23` | `SYSTEM_USER = "IA"` constant is declared but not used. |
| DC-004 | `em-app-as/docs/objectmapper-replacement.md` | Empty file (0 bytes). |

---

### 2.2 Frontend (`em-app-ui`)

#### Code Smells

| ID | Severity | Location | Description |
|----|----------|----------|-------------|
| CS-F001 | 🟡 Minor | `session-storage.service.ts` | Class is named `SessionStorageService` but calls `window.localStorage` throughout. Token, user info, and UI state stored in `localStorage`, not `sessionStorage`. |
| CS-F002 | 🟡 Minor | `session-storage.service.ts:25` | `clear()` method calls `window.localStorage.clear()` — clears ALL localStorage for the origin, including any third-party data. Should use targeted key removal. |
| CS-F003 | 🟡 Minor | `auth.guard.ts` | The return type signature is overly verbose: `Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree`. Modern Angular allows `boolean | UrlTree`. |
| CS-F004 | 🔵 Info | Multiple components | Abstract components (`AbstractEditComponent`, `AbstractIndexComponent`) are very large (estimated 200+ lines each). Consider splitting lifecycle and data-access concerns. |
| CS-F005 | 🔵 Info | `common.datasource.ts` | Custom `CommonDataSource` extending Angular CDK `DataSource` — ensure `connect()` / `disconnect()` contracts are properly implemented to avoid memory leaks. |

#### Duplications

| ID | Location | Description |
|----|----------|-------------|
| DUP-F001 | All feature services (`user.service.ts`, `account.service.ts`, etc.) | Services share identical `getX()`, `createX()`, `updateX()`, `deleteX()` patterns. A generic `CrudService<T>` base class would reduce duplication. |
| DUP-F002 | `abstract-edit.component.ts`, `abstract-index.component.ts` | Both abstract components likely duplicate subscription management boilerplate. |

#### Naming Conventions

| ID | Location | Issue |
|----|----------|-------|
| NC-F001 | `session-storage.service.ts` | As noted: class name contradicts implementation. |
| NC-F002 | `app-module.ts`, `app-routing-module.ts` | Angular 21 promotes standalone components. Existence of `NgModule`-style files alongside them warrants a migration plan. |

#### SOLID Violations

| ID | Principle | Location | Description |
|----|-----------|----------|-------------|
| SOLID-F001 | **Single Responsibility** | `AbstractEditComponent` | Handles CREATE, EDIT, VIEW, DELETE, and DUPLICATE in one component. Mode logic inflates the component. |

---

## 3. Security Analysis

### 3.1 Backend Security

#### 🔴 CRITICAL — Hardcoded OAuth2 Credentials

**File:** `em-app-as/src/main/resources/application.yml`

```yaml
google:
  client-id: 693388563044-s51abe3b7epuk7ldpin7kc3fs29s6pfo.apps.googleusercontent.com
  client-secret: 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  # COMMITTED SECRET
github:
  client-id: Ov23liBNoJCrAK4844Xz
  client-secret: 10ea9bcb13aa4d490278b8fe10f20c0c4ceb4e31  # COMMITTED SECRET
```

**Risk:** Any person with repository read access has these credentials. The GitHub and Google secrets are live and must be rotated immediately.  
**Fix:** Remove default fallback values from `${GOOGLE_CLIENT_SECRET:...}`. Rely solely on environment variables.

---

#### 🔴 CRITICAL — Hardcoded Basic-Auth in Apache Camel Route

**File:** `em-app-ig/src/main/java/ca/bigmwaj/emapp/ig/route/DeadLetterSenderRoute.java`

```java
.setHeader("Authorization").constant("Basic dGVzdDp0ZXN0")
// Base64 decodes to: test:test
```

**Risk:** Integration gateway authenticates to the application server using a hardcoded credential pair `test:test`. Any user can authenticate to internal endpoints with this credential. This is a backdoor.  
**Fix:** Replace with an environment-variable-backed configuration value and use a strong, rotated credential.

---

#### 🔴 HIGH — Weak / Hardcoded JWT Secret

**File:** `em-app-as/src/main/resources/application.yml`

```yaml
app:
  jwt:
    secret: ${JWT_SECRET:ABCD1234!@#$abcd?^kkkkkkkkkkuuuuuuuuuujghgfsssrfgtyhb}
```

**Risk:** The default fallback secret is weak and committed to source. If `JWT_SECRET` is not set in a deployment environment, all JWTs are signed with this known value, enabling token forgery.  
**Fix:** Remove the default fallback. Application must fail to start if `JWT_SECRET` is not provided.

---

#### 🟠 HIGH — SQL Logging Enabled (`show-sql: true`)

**File:** `em-app-as/src/main/resources/application.yml`

```yaml
spring:
  jpa:
    show-sql: true
```

**Risk:** SQL queries (including parameter values in some ORM configurations) appear in application logs in production, potentially leaking sensitive data (usernames, encrypted passwords in queries, etc.).  
**Fix:** Set `show-sql: false` or restrict to `dev` profile only.

---

#### 🟠 HIGH — Exception Stack Traces Logged via `logger.error(..., ex)`

**File:** `GlobalExceptionHandler.java`

All exception handlers log the full stack trace for every exception, including `NoResourceFoundException` (a routine 404). In production, detailed stack traces can reveal internal class names, library versions, and data layer structure to an attacker reading logs.  
**Fix:** Log full stack traces only for `5xx` errors. For `4xx` errors, log a brief message without the stack trace.

---

#### 🟡 MEDIUM — CORS Allows Two Origins

**File:** `SecurityConfig.java`

```java
configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://localhost:3000"));
```

`localhost:3000` is allowed but no component of the application is documented to run on port 3000. In production deployments this should be restricted to the actual frontend domain only.

---

#### 🟡 MEDIUM — OAuth2 Redirect URI Wildcards

The `redirect-uri` uses `{baseUrl}` and `{registrationId}` placeholders. While Spring handles these safely, ensure production deployments explicitly restrict redirect URIs in the OAuth2 provider's console to prevent open-redirect abuse.

---

#### 🟡 MEDIUM — No Rate Limiting

There is no rate-limiting middleware protecting `/auth/login` or the OAuth2 endpoints. A brute-force attack against `/auth/login` is possible.

---

#### 🔵 INFO — No Password Complexity Policy

`UserService.create()` accepts any non-empty string as a password. There is no minimum length, character diversity, or breach-detection check.

---

#### 🔵 INFO — No Token Revocation / Refresh Mechanism

JWT tokens are valid for 24 hours (`JWT_EXPIRATION: 86400000`). There is no mechanism to revoke a token before expiry (e.g., after password change or logout). A stolen token remains valid for up to 24 hours.

---

### 3.2 Frontend Security

#### 🔴 HIGH — JWT Token Stored in `localStorage`

**File:** `em-app-ui/src/app/core/services/session-storage.service.ts`

```typescript
private setItem<T>(key: string, value: T, mapper: (t: T) => string) {
  window.localStorage.setItem(key, mapper(value));
}
```

All authentication tokens (JWT, CSRF token, user info) are persisted in `localStorage`, which is accessible by any JavaScript running on the page. A single XSS vulnerability would allow an attacker to steal all tokens.  
**Fix:** Use `httpOnly` cookies for token storage, managed by the backend. If `localStorage` must be used, implement a Content Security Policy (CSP) to reduce XSS risk.

---

#### 🟠 HIGH — `SessionStorageService.clear()` Nukes All `localStorage`

**File:** `session-storage.service.ts:22`

```typescript
clear(): void {
  window.localStorage.clear();
}
```

Calling `clear()` (e.g., on logout) removes all `localStorage` entries for the origin, including any data belonging to third-party scripts. Use targeted `removeItem()` calls instead.

---

#### 🟡 MEDIUM — API Base URL Hardcoded in Development Environment

**File:** `em-app-ui/src/environments/environment.ts`

```typescript
apiUrl: 'http://localhost:8080'
```

`localhost:8080` is the only supported configuration for development. Teams sharing the repo cannot override this without modifying tracked files. Use a `.env` file pattern or Angular-specific environment injection.

---

#### 🔵 INFO — No Content Security Policy (CSP)

`em-app-ui/src/index.html` does not define a `<meta http-equiv="Content-Security-Policy">` header. Without a CSP, any injected script can run freely in the browser context.

---

## 4. Performance Analysis

### 4.1 Backend Performance

#### 🔴 HIGH — Double Entity Save

**File:** `UserService.java:55–56`

```java
entity = dao.save(entity);
dto = GlobalPlatformMapper.INSTANCE.toDto(dao.save(entity));
```

`dao.save(entity)` is called twice in sequence. The first result is captured into `entity`, but then `entity` is saved again immediately. This generates two `INSERT` or `UPDATE` SQL statements for a single create operation, adding unnecessary database load.  
**Fix:** `dto = GlobalPlatformMapper.INSTANCE.toDto(dao.save(entity));` — single save.

---

#### 🟡 MEDIUM — `searchAll()` Without Pagination

**File:** `AbstractMainService.java`

```java
public SearchResultDto<D> searchAll() {
  var r = getDao().findAll().stream()
      .map(this.getEntityToDtoMapper())
      .toList();
  return new SearchResultDto<>(r);
}
```

`findAll()` loads the entire table into memory. For tables with thousands of rows this will cause out-of-memory errors and slow responses. This method should either be removed or replaced with a paginated variant.

---

#### 🟡 MEDIUM — No Caching Strategy

`findById()` and `searchAll()` query the database on every call. For reference/lookup data (Privileges, Roles, LVOs) that rarely change, Spring's `@Cacheable` with an in-memory cache (Caffeine) would significantly reduce database pressure.

---

#### 🟡 MEDIUM — Kafka Publish Inside `@Transactional` Method

**File:** `UserService.java`

The `create()` method is annotated `@Transactional` and calls `kafkaPublisher.publish(...)` at the end. If the Kafka publish fails, the transaction has already been committed (Kafka is outside the JPA transaction). If the transaction rolls back after the Kafka publish (e.g., due to a `RuntimeException`), the event is already in the broker but the database record is missing — a **split-brain scenario**.  
**Fix:** Use `@TransactionalEventListener` or a transactional outbox pattern to publish events only after successful commit.

---

#### 🔵 INFO — No Connection Pool Configuration (HikariCP)

The `application.yml` does not configure HikariCP pool size (`maximum-pool-size`, `minimum-idle`). Spring Boot uses defaults (10 connections), which may be insufficient under load.

---

### 4.2 Frontend Performance

#### 🟡 MEDIUM — No Lazy Loading for Platform Module

**File:** `app-routing-module.ts`

The `PlatformModule` is lazy-loaded (`loadChildren: () => import(...).then(...)`), which is correct. Verify that all feature sub-modules within Platform are also lazy-loaded to prevent bundling all feature code into a single chunk.

---

#### 🟡 MEDIUM — No `OnPush` Change Detection Strategy

The abstract components (`AbstractEditComponent`, `AbstractIndexComponent`) and feature components do not appear to use `ChangeDetectionStrategy.OnPush`. Angular's default `CheckAlways` strategy re-renders components on every async event. For data-heavy list pages this can cause visible performance degradation.

---

#### 🔵 INFO — No HTTP Request Debouncing on Search

If any search fields trigger HTTP calls on every keystroke (vs. `debounceTime` + `distinctUntilChanged`), the frontend will issue excessive API requests. Verify that `search-form.component.ts` uses RxJS operators to throttle input.

---

## 5. Architecture Review

### 5.1 Backend Architecture

#### Layer Separation

```
HTTP Request → Controller (API layer)
                    ↓
             Service (Business Logic)
                    ↓
              DAO (Data Access)
                    ↓
             Entity (JPA / MySQL)
```

**Assessment:** ✅ Clean and consistent layering across all platform entities. Controllers do not contain business logic. DAOs are pure data-access interfaces. Services hold all mutation and validation logic.

**Violation:** `UserService` also implements `AuthenticationManager`, pulling security-framework concerns into the business layer.

---

#### Package Structure

```
ca.bigmwaj.emapp.as
├── api/            ← Presentation layer
│   ├── auth/       ← Auth-specific controllers + security beans
│   └── platform/  ← Domain controllers
├── service/        ← Business logic
│   └── platform/
├── dao/            ← Persistence (JPA repositories)
│   └── platform/
├── entity/         ← JPA entities
│   ├── common/     ← Abstract base entities
│   └── platform/
├── dto/            ← DTOs and MapStruct mappers
├── validator/      ← Custom validation engine (XML rules)
├── converter/      ← Spring type converters
├── integration/    ← Kafka publisher
└── lvo/            ← List Value Objects (enums)
```

**Assessment:** ✅ Well-structured. The `validator/xml/` sub-package housing the XML rules engine is a thoughtful design. Shared infrastructure (`em-app-dm`) cleanly separates shared types from module-specific types.

---

#### Microservice Readiness

| Aspect | Status | Notes |
|--------|--------|-------|
| Stateless API | ✅ | JWT-based, no server-side sessions |
| Externalized config | ⚠️ Partial | Secrets still in application.yml defaults |
| Service registry | ❌ Missing | No Eureka / Consul integration |
| Circuit breaker | ❌ Missing | No Resilience4j / Hystrix |
| Health checks | ✅ | `/actuator/health` exposed |
| Containerization | ⚠️ Partial | `docker-compose.yml` for DB only; no app Dockerfiles |

---

#### Exception Handling Strategy

**Assessment:** Partially complete.  
- ✅ `GlobalExceptionHandler` with `@RestControllerAdvice` handles all major exception types.  
- ⚠️ `NoSuchElementException` from `AbstractMainService.findById()` is not explicitly handled — it falls through to the generic `500` handler.  
- ⚠️ `ServiceException` is declared but not caught in the global handler.  
- ⚠️ Error responses lack a consistent structured format (body is sometimes plain `String`, sometimes HTML-formatted `<ul>` list).  

**Recommendation:** Define a standard `ErrorResponse` DTO (code, message, timestamp, path) and use it consistently across all error handlers. Add explicit handlers for `NoSuchElementException` (→ 404) and `ServiceException` (→ 422 or 400).

---

#### Configuration Management

| Item | Status |
|------|--------|
| Environment variable substitution | ✅ Used throughout |
| Secret management (vault/external) | ❌ Not implemented |
| Per-profile config (`application-prod.yml`) | ❌ Missing |
| Config server | ❌ Not used |

---

### 5.2 Frontend Architecture

#### Module Structure

```
app/
├── core/           ← Singleton services, guards, interceptors (loaded once)
├── features/
│   ├── platform/   ← Domain feature module (lazy-loaded)
│   └── shared/     ← Shared components, abstracts, datasource
└── app-routing-module.ts
```

**Assessment:** ✅ Correct Core/Feature/Shared pattern. Core module properly provides root-level singletons. Platform module is lazy-loaded.

---

#### State Management Strategy

**Assessment:** ⚠️ No formal state management.  
The application uses RxJS `BehaviorSubject` in `AuthService` for authentication state. Feature-level data state (user lists, account data) is managed locally within components via direct service calls. For the current scale this is acceptable, but as features grow, the absence of a centralized store (NgRx, Akita, or Signal Store) will lead to inconsistent data across components.

---

#### Component Reuse

**Assessment:** ✅ Strong reuse pattern.  
`AbstractEditComponent` and `AbstractIndexComponent` are effectively base classes for all CRUD UIs. Platform feature components inherit and extend these. `CommonDataSource` implements `DataSource<T>` for Angular Material tables, used across all list views.

---

#### Service Abstraction

**Assessment:** ✅ Clean service layer.  
Each domain has a dedicated Angular service (`user.service.ts`, `account.service.ts`, etc.) that encapsulates all HTTP calls. The `JwtInterceptor` transparently injects auth headers. The `ErrorInterceptor` handles 401 globally.  
**Improvement opportunity:** Extract a generic `CrudService<T, ID>` base class to avoid the structural duplication across domain services.

---

#### Routing Structure

```
/login              ← Public
/oauth/callback     ← Public
/                   ← Protected (LayoutComponent + AuthGuard)
  /dashboard
  /platform/users
  /platform/accounts
  /platform/contacts
  /platform/groups
  /platform/roles
  /platform/privileges
  /platform/dead-letters
```

**Assessment:** ✅ Clean routing with `AuthGuard` protecting all platform routes. Lazy loading applied to `PlatformModule`.

---

## 6. Testing Quality

### 6.1 Unit Test Coverage Assessment

| Module | Test Classes | Estimated Coverage | Notes |
|--------|--------------|--------------------|-------|
| `validator/xml` | 4 classes | ~80% | Comprehensive — `ValidationXmlParserTest`, `RuleFactoryTest`, etc. |
| `validator/rule` | 8 classes | ~75% | Good rule-level coverage |
| `validator/shared` | 4 classes | ~70% | Clause pattern validators well tested |
| `service/platform` | 3 integration tests | ~30% | `UserService`, `GroupService`, `RoleService` — only integration tests, no unit tests |
| `api/` (Controllers) | 0 classes | ~0% | **No controller tests** — critical gap |
| `dto/` (Mappers) | 3 classes | ~40% | `AbstractDtoValidatorTest`, `ContactDtoValidatorTest`, `AccountDtoValidatorTest` |
| `integration/` (Kafka) | 0 classes | ~0% | No tests for `KafkaPublisher` |
| `em-app-ui` | 1 spec file | ~5% | `app.spec.ts` exists but provides near-zero coverage |

---

### 6.2 Mocking Strategy

**Assessment:** ✅ Good mocking strategy for backend.  
The 15+ test builder classes (`UserDtoBuilder`, `AccountDtoBuilder`, etc.) provide reusable test fixtures — this is a mature pattern. Integration tests use H2 in-memory database correctly.  
**Issue:** `UserService` unit tests would be difficult due to `PasswordEncoder` being instantiated with `new BCryptPasswordEncoder()` instead of being injected. Direct instantiation prevents easy mocking.

---

### 6.3 Missing Integration Tests

| Gap | Impact |
|----|--------|
| No controller-layer tests (MockMvc) | Cannot verify HTTP status codes, response schemas, or auth behaviour |
| No JWT authentication flow test | Token generation / validation untested at the API level |
| No OAuth2 callback tests | OAuth success handler logic untested |
| No Kafka publish tests | Async event publishing untested; split-brain risk undetected |
| No Camel route tests | `DeadLetterSenderRoute` logic untested |

---

### 6.4 Frontend Test Gaps

| Gap | Impact |
|----|--------|
| `app.spec.ts` minimal | Only bootstrapping checked; no service, guard, or interceptor tests |
| No `AuthGuard` tests | Route protection logic not validated |
| No `JwtInterceptor` tests | Token attachment and auth header behaviour not verified |
| No `AuthService` tests | Login/logout/OAuth callback flows not tested |
| No component tests | All Angular Material components untested |

---

## 7. Recommendation Document

### 7.1 Immediate Fixes (High Priority)

| # | Action | File(s) | Effort |
|---|--------|---------|--------|
| 1 | **Rotate and externalize OAuth2 secrets** — remove default fallback values; revoke exposed Google/GitHub credentials | `application.yml` | 1h |
| 2 | **Replace hardcoded `Basic dGVzdDp0ZXN0`** in Camel route with env-var-backed credentials | `DeadLetterSenderRoute.java` | 2h |
| 3 | **Remove JWT secret default fallback** — fail fast on startup if `JWT_SECRET` not set | `application.yml`, `JwtTokenProvider.java` | 1h |
| 4 | **Fix double `dao.save(entity)`** in `UserService.create()` | `UserService.java` | 30m |
| 5 | **Disable `show-sql`** in production or move to dev profile only | `application.yml` | 15m |
| 6 | **Add `NoSuchElementException` → 404 handler** to `GlobalExceptionHandler` | `GlobalExceptionHandler.java` | 1h |

---

### 7.2 Medium-Term Refactoring

| # | Action | Effort |
|---|--------|--------|
| 1 | **Inject `PasswordEncoder` via Spring** — remove `new BCryptPasswordEncoder()` in `UserService` | 2h |
| 2 | **Extract `AuthenticationManager` to its own class** — resolve SRP violation in `UserService` | 4h |
| 3 | **Rename `SessionStorageService` to `LocalStorageService`** or switch to `sessionStorage` | 2h |
| 4 | **Create `application-prod.yml`** and `application-dev.yml` — separate concerns by profile | 3h |
| 5 | **Standardize error response format** — create `ErrorResponse` DTO; update `GlobalExceptionHandler` | 4h |
| 6 | **Remove dead code** — commented-out `@Bean` methods, `EmailSenderRoute`, `SYSTEM_USER` constant, empty `.md` file | 1h |
| 7 | **Create `CrudService<T>` in Angular** — eliminate duplication across `user.service.ts`, etc. | 4h |
| 8 | **Add rate limiting** to `/auth/login` (Spring Boot `spring-boot-starter-actuator` + bucket4j or similar) | 1 day |
| 9 | **Wrap Kafka publish in transactional outbox pattern** to avoid split-brain on rollback | 1 day |
| 10 | **Add `ChangeDetectionStrategy.OnPush`** to Angular list and edit components | 3h |

---

### 7.3 Long-Term Architectural Improvements

| # | Action | Effort |
|---|--------|--------|
| 1 | **Move JWT to `httpOnly` cookies** — eliminates XSS token theft risk in Angular frontend | 3 days |
| 2 | **Implement token refresh** — short-lived access tokens + refresh token rotation | 3 days |
| 3 | **Add Redis-based token revocation list** — allows logout before expiry | 2 days |
| 4 | **Introduce Spring Profiles** for full dev/staging/prod config separation | 1 day |
| 5 | **Integrate a secrets manager** (HashiCorp Vault, AWS Secrets Manager) for credentials | 2 days |
| 6 | **Add `NgRx Signal Store` or `Akita`** for centralized Angular state management | 1 week |
| 7 | **Write `MockMvc` controller tests** for all API endpoints | 1 week |
| 8 | **Implement Content Security Policy** in Angular frontend | 1 day |
| 9 | **Add Dockerfiles** for `em-app-as` and `em-app-ig` — complete containerization | 1 day |
| 10 | **Upgrade Short ID types** to `Integer` or `Long` to prevent row-count ceiling | 2 days |

---

### 7.4 DevOps Improvements

| # | Action |
|---|--------|
| 1 | Add CI/CD pipeline (GitHub Actions) with build, test, and SonarQube scan stages |
| 2 | Add Dockerfile for `em-app-as` and `em-app-ig`; create `docker-compose.prod.yml` |
| 3 | Add `docker-compose` health checks for database readiness before app startup |
| 4 | Introduce `OWASP Dependency-Check` Maven plugin to scan for vulnerable dependencies |
| 5 | Configure `Jacoco` for backend test coverage reporting with a minimum threshold gate |
| 6 | Integrate Vitest coverage reporting for Angular frontend |
| 7 | Set up centralized log aggregation (ELK Stack or Grafana Loki) |

---

### 7.5 Code Quality Gates Proposal

The following gates should be enforced before merging any pull request:

| Gate | Threshold |
|------|-----------|
| Unit test coverage (backend) | ≥ 70% |
| Unit test coverage (frontend) | ≥ 60% |
| No new Critical or Blocker issues | 0 new issues |
| No committed secrets | 0 (GitLeaks scan) |
| No duplicated code blocks | < 5% duplication |
| Cyclomatic complexity per method | ≤ 10 |
| Build passes | Required |
| Linter passes (ESLint) | Required |
| SonarQube Quality Gate | Pass |

---

*Report generated from static code analysis of repository `bigmwaj/em-app` — all findings are based on observable code patterns. Coverage figures are estimates; exact numbers require instrumented test execution.*
