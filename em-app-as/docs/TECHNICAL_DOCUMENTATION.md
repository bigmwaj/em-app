# app-server Technical Documentation (`em-app-as`)

**Module:** `em-app-as` — Application Server  
**Type:** Spring Boot REST API  
**Version:** Spring Boot 4.0.1 / Java 21  
**Last Updated:** 2026-03-01  

---

## Table of Contents

1. [Technical Overview](#1-technical-overview)
2. [Architecture Description](#2-architecture-description)
3. [Key Modules Explanation](#3-key-modules-explanation)
4. [API Structure](#4-api-structure)
5. [Configuration Reference](#5-configuration-reference)
6. [How to Run Locally](#6-how-to-run-locally)
7. [How to Build & Deploy](#7-how-to-build--deploy)
8. [Dependency Overview](#8-dependency-overview)

---

## 1. Technical Overview

`em-app-as` is the backend REST API for the Elite Maintenance Application. It provides:

- **Authenticated CRUD operations** for platform domain objects: Users, Accounts, Contacts, Groups, Roles, Privileges, and Dead Letters.
- **Dual authentication**: username/password with BCrypt + JWT, and OAuth2 social login (Google, GitHub, Facebook).
- **Advanced search API** with dynamic `WHERE` clause construction, multi-field sorting, and server-side pagination.
- **Kafka event publishing** for async downstream processing.
- **XML-driven validation rules engine** for complex, configurable DTO validation.

### Technology Stack

| Layer | Technology |
|-------|-----------|
| Runtime | Java 21 |
| Framework | Spring Boot 4.0.1 |
| Security | Spring Security + OAuth2 Client + JWT (JJWT 0.12.6) |
| Persistence | Spring Data JPA + Hibernate / MySQL 8.1 |
| DTO Mapping | MapStruct 1.6.3 |
| Boilerplate | Lombok 1.18.42 |
| Messaging | Apache Kafka (Spring Kafka) |
| API Docs | SpringDoc OpenAPI 3 (Swagger UI) |
| Testing | JUnit 5, Mockito 5, AssertJ 4, H2 (in-memory) |
| Build | Maven 3.9+ |

---

## 2. Architecture Description

### Layered Architecture

```
┌──────────────────────────────────────────────────────────┐
│  HTTP Client (Angular UI / External API Consumer)        │
└────────────────────────┬─────────────────────────────────┘
                         │ HTTP/HTTPS
┌────────────────────────▼─────────────────────────────────┐
│  API Layer  (api/)                                        │
│  ┌─────────────────────────────────────────────────────┐ │
│  │ AuthenticationFilter (JWT / Basic Auth validation)  │ │
│  └─────────────────────────────────────────────────────┘ │
│  Controllers: UserController, AccountController,          │
│               ContactController, GroupController,         │
│               RoleController, PrivilegeController,        │
│               DeadLetterController, AuthController        │
│  GlobalExceptionHandler (@RestControllerAdvice)           │
└────────────────────────┬─────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────┐
│  Service Layer  (service/)                                │
│  AbstractMainService<D, E, ID>                            │
│  ├── UserService          ├── RoleService                 │
│  ├── AccountService       ├── PrivilegeService            │
│  ├── ContactService       ├── GroupService                │
│  ├── ContactEmailService  ├── GroupRoleService            │
│  ├── ContactPhoneService  ├── GroupUserService            │
│  ├── ContactAddressService├── UserRoleService             │
│  ├── AccountContactService└── DeadLetterService           │
│  └── RolePrivilegeService                                 │
└────────────────────────┬─────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────┐
│  DAO Layer  (dao/)                                        │
│  AbstractDao<E, ID> extends JpaRepository                 │
│  Dynamic JPQL queries via QueryConfig                     │
│  Per-entity DAO interfaces (16 total)                     │
└────────────────────────┬─────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────┐
│  Entity Layer  (entity/)                                  │
│  AbstractBaseEntity → AbstractChangeTrackingEntity        │
│                     → AbstractStatusTrackingEntity        │
│  21 JPA entities mapped to MySQL tables                   │
└────────────────────────┬─────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────┐
│  MySQL 8.1  (media_db)                                    │
└──────────────────────────────────────────────────────────┘

Cross-Cutting Concerns:
┌─────────────────────────────────┐
│  Validation Engine (validator/) │  ← XML-driven rule engine
│  MapStruct Mapper (dto/)        │  ← Compile-time DTO ↔ Entity
│  Kafka Publisher (integration/) │  ← Async event publishing
│  Security (api/auth/security/)  │  ← JWT + OAuth2 filter chain
└─────────────────────────────────┘
```

### Authentication Architecture

```
Username/Password Flow:
  POST /auth/login
     ↓
  AuthController → UserService.authenticate()
     ↓
  BCrypt password match
     ↓
  JwtTokenProvider.generateTokenForUser()
     ↓
  JWT returned in LoginResponse

OAuth2 Flow:
  GET /oauth2/authorization/{provider}
     ↓
  Spring Security redirects to provider
     ↓
  Provider callback → /login/oauth2/code/{registrationId}
     ↓
  PostOAuth2Authentication.loadUser()
     ↓
  OAuth2AuthenticationSuccessHandler
     ↓
  JWT generated and attached to redirect: /oauth/callback?token={jwt}
     ↓
  Frontend stores token and navigates to dashboard

Subsequent Requests:
  HTTP Request → AuthenticationFilter
     ↓
  Extract Bearer token from Authorization header
     ↓
  JwtTokenProvider.validateToken()
     ↓
  Set SecurityContext → Request reaches Controller
```

---

## 3. Key Modules Explanation

### 3.1 `api/` — Presentation Layer

| Class | Responsibility |
|-------|---------------|
| `AbstractBaseAPI` | Base class providing `_success()`, `_warn()`, `_error()` message factory helpers |
| `GlobalExceptionHandler` | `@RestControllerAdvice` — centralized HTTP error mapping for all exceptions |
| `AuthController` | `/auth/login`, `/auth/user`, `/auth/status` endpoints |
| `UserController` | Full CRUD + advanced search for platform users |
| `AccountController` | Account management CRUD |
| `ContactController` | Contact + contact points (email, phone, address) management |
| `GroupController` | Group management with user and role assignment |
| `RoleController` | Role management with privilege assignment |
| `PrivilegeController` | Privilege CRUD |
| `DeadLetterController` | Dead letter queue management |

**Controller pattern:**  
All controllers extend `AbstractBaseAPI`, are annotated `@RestController`, and follow a consistent endpoint structure: `GET /search`, `GET /id/{id}`, `POST /`, `PATCH /`, `DELETE /{id}`.

---

### 3.2 `api/auth/security/` — Security Beans

| Class | Responsibility |
|-------|---------------|
| `SecurityConfig` | Main Spring Security filter chain: CORS, CSRF, session policy, OAuth2, JWT filter |
| `AuthenticationFilter` | `OncePerRequestFilter` — validates JWT Bearer tokens and sets `SecurityContext` |
| `JwtTokenProvider` | Generates and validates JWT tokens using HMAC-SHA; configurable secret and expiration |
| `OAuth2AuthenticationSuccessHandler` | On successful OAuth2 login: generates JWT, redirects to frontend `/oauth/callback?token=` |
| `PostOAuth2Authentication` | `OAuth2UserService` — loads/creates user record after OAuth2 provider authentication |

---

### 3.3 `service/` — Business Logic Layer

#### `AbstractBaseService<D, E>`

Provides audit-trail helpers:
- `beforeCreateHistEntity(entity)` — sets `createdBy`, `createdDate`
- `beforeUpdateHistEntity(entity)` — sets `updatedBy`, `updatedDate`

#### `AbstractMainService<D, E, ID>`

Provides generic CRUD:
- `searchAll()` — load all records (use with caution on large tables)
- `search(DefaultSearchCriteria sc)` — paginated, filterable, sortable search
- `findById(ID id)` — find by primary key; throws `NoSuchElementException` if missing
- `deleteById(ID id)` — delete with lifecycle hooks (`beforeDelete`, `afterDelete`)

Concrete services override `getEntityToDtoMapper()` and `getDao()`, and add domain-specific logic.

---

### 3.4 `dao/` — Data Access Layer

#### `AbstractDao<E, ID>`

Extends `JpaRepository<E, ID>` with dynamic JPQL query capabilities:

- `findAllByCriteria(EntityManager em, AbstractSearchCriteria sc)` — builds and executes a paginated JPQL query based on `WhereClause` and `SortByClause` inputs.
- `countAllByCriteria(EntityManager em, AbstractSearchCriteria sc)` — corresponding count query for total-result pagination metadata.
- `getSpecialWhereClause(AbstractSearchCriteria sc)` — hook for DAO-specific additional predicates (e.g., `UserDao` overrides this to filter assignable users for role/group assignment).

#### `shared/QueryConfig`

Builder that assembles the JPQL query string and parameter map from `WhereClause` and `SortByClause` objects. Supports `AND` / `OR` join operators between WHERE clauses.

---

### 3.5 `entity/` — JPA Entity Layer

#### Base Entity Hierarchy

```
AbstractBaseEntity (id, getDefaultKey())
    │
    ├── AbstractChangeTrackingEntity (createdBy, createdDate, updatedBy, updatedDate)
    │       │
    │       └── AbstractStatusTrackingEntity (status, statusDate)
    │               │
    │               ├── UserEntity
    │               ├── AccountEntity
    │               ├── ContactEntity
    │               ├── GroupEntity
    │               ├── RoleEntity
    │               └── PrivilegeEntity
    │
    └── (Direct) DeadLetterEntity, AbstractContactPointEntity
```

**Notable entity patterns:**
- Composite primary keys implemented with separate `*PK` classes (e.g., `GroupRolePK`, `UserRolePK`) and `@EmbeddedId`.
- Collection relationships use `@Fetch(FetchMode.SUBSELECT)` to prevent N+1 queries.
- Sensitive fields (e.g., `password` in `UserEntity`) are annotated with `@JsonProperty(access = WRITE_ONLY)` to prevent exposure in serialization.

---

### 3.6 `dto/` — Data Transfer Objects

#### `GlobalPlatformMapper`

A MapStruct mapper interface providing compile-time-generated DTO ↔ Entity conversion methods for all platform types. Uses `@Mapper(componentModel = "spring")` for Spring bean injection.

Key mappings:
- `toDto(UserEntity)` / `toEntity(UserDto)` 
- `toDto(AccountEntity)` / `toEntity(AccountDto)` 
- Similar pairs for Contact, Group, Role, Privilege, DeadLetter

Custom qualified methods handle ID-only sub-object mapping (e.g., mapping `ContactEntity` to only its `id` field when nested in a parent DTO).

---

### 3.7 `validator/` — Validation Engine

The validation engine supports two approaches:

#### Annotation-Based Spring Validation

- `@ValidDto` — triggers `SpringDtoValidator` on a DTO parameter.
- `SpringDtoValidator` — reads an XML configuration file named after the DTO class (e.g., `UserDto.xml`) and applies the declared rules.

#### Clause Pattern Validation (Search API)

- `@ValidWhereClausePatterns` / `@ValidSortByClausePatterns` — annotations applied to controller method parameters.
- `WhereClausePatternsValidator` / `SortByClausePatternsValidator` — validate that submitted filter/sort fields match the declared `@WhereClauseSupportedField` / `@SortByClauseSupportedField` whitelist.

#### XML Rules Engine (`validator/xml/`)

| Class | Responsibility |
|-------|---------------|
| `ValidationXmlParser` | Parses `*Dto.xml` validation config files using DOM/XPath |
| `RuleFactory` | Instantiates rule objects from XML `<rule>` elements |
| `ConditionEvaluator` | Evaluates `<condition>` elements to enable/disable rules contextually |
| `ValidationNamespaceResolver` | XPath namespace prefix resolver for validation XML schema |

Built-in rules: `NonNullRule`, `NonBlankRule`, `NonEmptyRule`, `MaxLengthRule`, `MinRule`, `MaxRule`, `EmailRule`, `PhoneRule`, `EqualsRule`, plus domain-specific rules (`BirthDateRule`, `UniqueUsernameRule`, `RoleNameUniqueRule`, etc.).

---

### 3.8 `integration/` — Kafka Publisher

```java
// KafkaPublisher
kafkaPublisher.publish("user-created", userDto);
```

Wraps `KafkaTemplate` to publish domain events to named Kafka topics. Currently used in `UserService.create()` to notify downstream consumers (e.g., `em-app-ig`) of new user creation.

---

### 3.9 `lvo/` — List Value Objects

Enums representing static lookup values used throughout the domain. Each LVO enum is used as a JPA `@Enumerated(EnumType.STRING)` column.

| LVO | Values |
|-----|--------|
| `UserStatusLvo` | `ACTIVE`, `INACTIVE`, `PENDING` |
| `AccountStatusLvo` | `ACTIVE`, `INACTIVE` |
| `OwnerTypeLvo` | `ACCOUNT`, `SYSTEM`, `INDIVIDUAL` |
| `UsernameTypeLvo` | `EMAIL`, `PHONE`, `CUSTOM` |
| `PrivilegeLvo` | Platform-specific privileges |
| `AddressTypeLvo` | `HOME`, `WORK`, `OTHER` |
| `EmailTypeLvo` | `PRIMARY`, `SECONDARY`, `WORK` |
| `PhoneTypeLvo` | `MOBILE`, `HOME`, `WORK` |

---

## 4. API Structure

**Base URL:** `http://localhost:8080`  
**API Docs:** `http://localhost:8080/swagger-ui.html`

### Authentication Endpoints

| Method | Path | Auth Required | Description |
|--------|------|---------------|-------------|
| `POST` | `/auth/login` | No | Authenticate with username/password; returns JWT |
| `GET` | `/auth/user` | Yes | Returns current authenticated user's profile |
| `GET` | `/auth/status` | No | Returns authentication status (`true`/`false`) |
| `GET` | `/oauth2/authorization/{provider}` | No | Initiates OAuth2 redirect (`google`, `github`, `facebook`) |

**Login Request:**
```json
{
  "username": "user@example.com",
  "password": "secret"
}
```

**Login Response:**
```json
{
  "token": "<jwt>",
  "expiresIn": 86400000
}
```

---

### Platform CRUD Endpoints

All endpoints follow the pattern: `/api/v1/platform/{resource}`

#### Users — `/api/v1/platform/users`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/platform/users` | Search users with filters, pagination, and sorting |
| `GET` | `/api/v1/platform/users/id/{userId}` | Get user by ID |
| `POST` | `/api/v1/platform/users` | Create a new user |
| `PATCH` | `/api/v1/platform/users` | Update an existing user |
| `DELETE` | `/api/v1/platform/users/{userId}` | Delete user by ID |

**Search query parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `pageSize` | `Short` | Number of records per page (positive) |
| `pageIndex` | `Integer` | Zero-based page index |
| `whereClauseJoinOp` | `AND` \| `OR` | Join operator between WHERE clauses (default: `AND`) |
| `calculateStatTotal` | `boolean` | If true, includes total count in response |
| `filters` | `List<WhereClause>` | Filter clauses (see below) |
| `sortBy` | `List<SortByClause>` | Sort directives |
| `assignableToRoleId` | `Short` | Filter to users assignable to a specific role |
| `assignableToGroupId` | `Short` | Filter to users assignable to a specific group |

**Supported filter fields for Users:**  
`status`, `ownerType`, `username`, `firstName`, `lastName`, `phone`, `email`, `address`

**Search Result Response:**
```json
{
  "data": [...],
  "searchInfos": {
    "total": 42,
    "pageSize": 20,
    "pageIndex": 0
  }
}
```

#### Accounts — `/api/v1/platform/accounts`

Same CRUD pattern; supports filtering by `status`, `name`, `type`.

#### Contacts — `/api/v1/platform/contacts`

Includes nested management of contact points:
- `GET /api/v1/platform/contacts/{contactId}/emails`
- `GET /api/v1/platform/contacts/{contactId}/phones`
- `GET /api/v1/platform/contacts/{contactId}/addresses`

#### Groups — `/api/v1/platform/groups`

Includes role and user assignment sub-resources.

#### Roles — `/api/v1/platform/roles`

Includes privilege assignment sub-resources.

#### Privileges — `/api/v1/platform/privileges`

#### Dead Letters — `/api/v1/platform/dead-letters`

Used to track failed Kafka messages requiring manual intervention.

---

### Search Filter Format

Filters (`filters` parameter) use the `WhereClause` structure:

```
filters=field:OPERATOR:value
```

Examples:
```
filters=status:EQ:ACTIVE
filters=username:LIKE:john
filters=status:EQ:ACTIVE&filters=ownerType:EQ:ACCOUNT
```

Supported operators: `EQ`, `NEQ`, `LIKE`, `GT`, `LT`, `GTE`, `LTE`

---

## 5. Configuration Reference

Configuration file: `src/main/resources/application.yml`

### Database

| Property | Default | Description |
|----------|---------|-------------|
| `spring.datasource.url` | `${DB_URL:jdbc:mysql://localhost:3306/media_db}` | JDBC connection URL |
| `spring.datasource.username` | `${DB_USERNAME:media_db_user}` | Database username |
| `spring.datasource.password` | `${DB_PASSWORD:media_db_pswd}` | Database password |
| `spring.jpa.hibernate.ddl-auto` | `update` | Schema update strategy |
| `spring.jpa.show-sql` | `true` | Log SQL (⚠️ disable in production) |

### JWT

| Property | Default | Description |
|----------|---------|-------------|
| `app.jwt.secret` | `${JWT_SECRET:...}` | HMAC signing key (⚠️ must override via env var) |
| `app.jwt.expiration` | `${JWT_EXPIRATION:86400000}` | Token lifetime in milliseconds (default: 24h) |

### OAuth2

| Property | Description |
|----------|-------------|
| `spring.security.oauth2.client.registration.google.client-id` | Google OAuth2 app client ID |
| `spring.security.oauth2.client.registration.google.client-secret` | Google OAuth2 app secret (⚠️ use env var) |
| `spring.security.oauth2.client.registration.github.client-id` | GitHub OAuth2 app client ID |
| `spring.security.oauth2.client.registration.github.client-secret` | GitHub OAuth2 app secret (⚠️ use env var) |

### Application

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `8080` | HTTP server port |
| `app.front.url` | `http://localhost:4200` | Frontend base URL (for CORS) |
| `app.auth-success.redirect-uri` | `http://localhost:4200/oauth/callback` | OAuth2 post-auth redirect |

### Kafka

| Property | Default | Description |
|----------|---------|-------------|
| `spring.kafka.bootstrap-servers` | `localhost:9092` | Kafka broker address |
| `spring.kafka.producer.key-serializer` | `StringSerializer` | Message key serializer |
| `spring.kafka.producer.value-serializer` | `JsonSerializer` | Message value serializer |

### Required Environment Variables

```bash
JWT_SECRET=<64+ character random string>
DB_URL=jdbc:mysql://host:3306/media_db
DB_USERNAME=<db_user>
DB_PASSWORD=<db_password>
GOOGLE_CLIENT_ID=<google_client_id>
GOOGLE_CLIENT_SECRET=<google_client_secret>
GITHUB_CLIENT_ID=<github_client_id>
GITHUB_CLIENT_SECRET=<github_client_secret>
```

---

## 6. How to Run Locally

### Prerequisites

- Java 21+
- Maven 3.9+
- MySQL 8.0+ (or Docker)
- Kafka 3.0+ (optional — app starts without Kafka, publishes silently fail)

### Step 1: Start the Database

**Using Docker (recommended):**
```bash
# From the repository root
docker-compose up -d
```

**Manual MySQL setup:**
```sql
CREATE DATABASE media_db;
CREATE USER 'media_db_user'@'localhost' IDENTIFIED BY 'media_db_pswd';
GRANT ALL PRIVILEGES ON media_db.* TO 'media_db_user'@'localhost';
FLUSH PRIVILEGES;
```

### Step 2: Set Environment Variables

```bash
export JWT_SECRET="$(openssl rand -base64 64)"
export DB_URL="jdbc:mysql://localhost:3306/media_db"
export DB_USERNAME="media_db_user"
export DB_PASSWORD="media_db_pswd"

# Optional — for OAuth2 login
export GOOGLE_CLIENT_ID="your-google-client-id"
export GOOGLE_CLIENT_SECRET="your-google-secret"
export GITHUB_CLIENT_ID="your-github-client-id"
export GITHUB_CLIENT_SECRET="your-github-secret"
```

### Step 3: Build the Project

```bash
# From the repository root (builds all modules)
mvn clean install -DskipTests

# Or build only app-server and its dependencies
mvn clean install -DskipTests -pl em-app-as -am
```

### Step 4: Run the Application

```bash
cd em-app-as
mvn spring-boot:run
```

The server starts on `http://localhost:8080`.  
Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

### Step 5: Verify

```bash
# Health check
curl http://localhost:8080/actuator/health

# Authentication test
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@example.com","password":"secret"}'
```

### Debug Mode

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

---

## 7. How to Build & Deploy

### Build for Production

```bash
# From repository root
mvn clean package -DskipTests -P prod

# Output JAR
ls em-app-as/target/em-app-as-*.jar
```

### Run the Packaged JAR

```bash
java -jar em-app-as/target/em-app-as-*.jar \
  --spring.datasource.url=$DB_URL \
  --spring.datasource.username=$DB_USERNAME \
  --spring.datasource.password=$DB_PASSWORD \
  --app.jwt.secret=$JWT_SECRET
```

### Docker (Manual — Dockerfile not yet included)

Create `em-app-as/Dockerfile`:
```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/em-app-as-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:
```bash
cd em-app-as
mvn clean package -DskipTests
docker build -t em-app-as:latest .
docker run -p 8080:8080 \
  -e JWT_SECRET="$JWT_SECRET" \
  -e DB_URL="$DB_URL" \
  -e DB_USERNAME="$DB_USERNAME" \
  -e DB_PASSWORD="$DB_PASSWORD" \
  em-app-as:latest
```

### Production Checklist

- [ ] Set all required environment variables (no default fallbacks)
- [ ] Set `spring.jpa.show-sql=false`
- [ ] Set `spring.jpa.hibernate.ddl-auto=validate` or `none`
- [ ] Update CORS allowed origins to production frontend URL
- [ ] Update OAuth2 redirect URIs in provider consoles
- [ ] Enable HTTPS (TLS termination at load balancer or in Spring)
- [ ] Set appropriate JWT expiration (shorter for higher security)
- [ ] Configure HikariCP pool size for expected load
- [ ] Set up log aggregation (no `System.out` logging in production)
- [ ] Enable Spring Actuator with appropriate security

---

## 8. Dependency Overview

### Production Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| `spring-boot-starter-web` | 4.0.1 | REST API, embedded Tomcat |
| `spring-boot-starter-data-jpa` | 4.0.1 | JPA / Hibernate ORM |
| `spring-boot-starter-security` | 4.0.1 | Authentication and authorization |
| `spring-boot-starter-oauth2-client` | 4.0.1 | OAuth2 social login |
| `spring-boot-starter-validation` | 4.0.1 | Bean Validation (Jakarta) |
| `spring-boot-starter-actuator` | 4.0.1 | Health checks and metrics |
| `spring-kafka` | (managed) | Apache Kafka producer |
| `jjwt-api` + `jjwt-impl` + `jjwt-jackson` | 0.12.6 | JWT generation and validation |
| `mysql-connector-j` | 9.5.0 | MySQL JDBC driver |
| `mapstruct` | 1.6.3 | Compile-time DTO ↔ Entity mapping |
| `lombok` | 1.18.42 | Boilerplate code generation |
| `springdoc-openapi-starter-webmvc-ui` | (latest) | Swagger UI / OpenAPI 3 docs |

### Test Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| `spring-boot-starter-test` | 4.0.1 | JUnit 5, Mockito, MockMvc |
| `h2` | (managed) | In-memory database for integration tests |
| `mockito-core` | 5.21.0 | Mocking framework |
| `assertj-core` | 4.0.0-M1 | Fluent assertion library |

### Shared Modules

| Module | Purpose |
|--------|---------|
| `em-app-dm` | Shared DTOs (`AbstractBaseDto`, `AbstractChangeTrackingDto`, `SharedDeadLetterDto`) and LVOs (`DeadLetterStatusLvo`, `EditActionLvo`) |

### Dependency Notes

- **MapStruct + Lombok order matters**: In `pom.xml`, the MapStruct annotation processor must be declared *after* Lombok in the compiler plugin configuration, otherwise Lombok-generated getters/setters won't be visible to MapStruct.
- **JJWT 0.12.6** uses the builder API; the older `Jwts.parser().setSigningKey()` API is removed. The codebase uses the current `Jwts.parser().verifyWith()` approach.
- **Spring Boot 4.0.1** requires Java 21 and is built on Spring Framework 7.x (Jakarta EE 11 namespace: `jakarta.*` not `javax.*`).
