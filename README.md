# Elite Maintenance Application (em-app)

A modern full-stack enterprise application for managing users, accounts, and contacts with comprehensive OAuth2 authentication and JWT-based security.

## 🏗️ Architecture Overview

This is a three-tier application with:
- **Frontend**: Angular 21 SPA with Material Design
- **Backend**: Spring Boot 4.0.1 REST API with OAuth2/JWT
- **Database**: MySQL 8.1+ with JPA/Hibernate ORM

```
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│  Angular 21 UI  │ ←──→ │ Spring Boot API │ ←──→ │   MySQL 8.1+    │
│   Port 4200     │      │   Port 8080     │      │   Port 3306     │
└─────────────────┘      └─────────────────┘      └─────────────────┘
```

## 📁 Project Structure

```
em-app/
├── em-app-as/              # Application Server (Spring Boot)
│   ├── src/main/java/      # Controllers, Services, DAOs
│   └── src/main/resources/ # application.yml, configs
├── em-app-dm/              # Data Model (JPA Entities)
│   └── src/main/java/      # Entity classes, enums
├── em-app-ui/              # Angular Frontend Application
│   ├── src/app/core/       # Core components, services
│   ├── src/app/features/   # Feature modules, components
│   └── src/environments/   # Environment configs
├── local-infra/            # Local infrastructure setup
├── docker-compose.yml      # MySQL + Workbench containers
└── pom.xml                 # Multi-module Maven parent
```

## 🚀 Quick Start

### Prerequisites
- **Java 21** (OpenJDK recommended)
- **Node.js 20+** with npm 11.6.2+
- **Maven 3.9+**
- **MySQL 8.1+** (or use Docker Compose)

### 1. Start Database (Docker)

```bash
docker-compose up -d
```

This starts MySQL on port 3306 with credentials:
- Database: `media_db`
- Username: `media_db_user`
- Password: `media_db_pswd`

### 2. Start Backend (Spring Boot)

```bash
# Build all modules
mvn clean install

# Run the application server
cd em-app-as
mvn spring-boot:run
```

Backend will be available at **http://localhost:8080**
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- API Docs: http://localhost:8080/v3/api-docs

### 3. Start Frontend (Angular)

```bash
# Navigate to UI directory
cd em-app-ui

# Install dependencies (first time)
npm install

# Start development server
npm start
```

Frontend will be available at **http://localhost:4200**

## ✨ Features

### Backend Features
- ✅ **Spring Boot 4.0.1** with Java 21
- ✅ **OAuth2 Authentication** (Google, GitHub, Facebook, TikTok)
- ✅ **JWT Token Management** (HMAC-SHA256, 24-hour expiry)
- ✅ **RESTful API** with OpenAPI/Swagger documentation
- ✅ **JPA/Hibernate** ORM with MySQL
- ✅ **MapStruct** for DTO ↔ Entity mapping
- ✅ **Global Exception Handling** with @RestControllerAdvice
- ✅ **Advanced Search/Filter** capabilities
- ✅ **Transaction Management** with @Transactional

### Frontend Features
- ✅ **Angular 21** with TypeScript 5.9+
- ✅ **Angular Material Design** components
- ✅ **JWT Authentication** with HTTP interceptors
- ✅ **Route Guards** for protected pages
- ✅ **RxJS** for reactive data management
- ✅ **Responsive Layout** (mobile + desktop)
- ✅ **OAuth 2.0 Login** integration
- ✅ **Form Validation** and error handling

### Core Domain Entities
- 👤 **Users** - Authentication and user management
- 🏢 **Accounts** - Account management with status tracking
- 📇 **Contacts** - Contact information with phone/email/address
- 🔗 **Account-Contact Relationships** - Many-to-many associations

## 📚 API Endpoints

### Authentication
```
GET  /auth/user            # Get current authenticated user
GET  /auth/status          # Check authentication status
```

### User Management
```
GET    /api/v1/platform/user                    # List all users
GET    /api/v1/platform/user/user-id/{id}      # Get user by ID
POST   /api/v1/platform/user                    # Create user
PATCH  /api/v1/platform/user                    # Update user
DELETE /api/v1/platform/user/user-id/{id}      # Delete user
```

### Account Management
```
GET    /api/v1/platform/account                 # List accounts
GET    /api/v1/platform/account/account-id/{id} # Get account
POST   /api/v1/platform/account                 # Create account
PATCH  /api/v1/platform/account                 # Update account
DELETE /api/v1/platform/account/account-id/{id} # Delete account
```

### Contact Management
```
GET    /api/v1/platform/contact                 # List contacts
GET    /api/v1/platform/contact/contact-id/{id} # Get contact
POST   /api/v1/platform/contact                 # Create contact
PATCH  /api/v1/platform/contact                 # Update contact
DELETE /api/v1/platform/contact/contact-id/{id} # Delete contact
```

## 🔐 Security

- **OAuth2 Providers**: Google, GitHub, Facebook, TikTok
- **JWT Token**: HMAC-SHA256, 256-bit key, 24-hour expiry
- **CORS**: Configured for localhost:4200
- **Stateless**: No server-side sessions
- **Protected Endpoints**: All /api/** routes require authentication

⚠️ **Security Notes**:
- Passwords should be hashed with BCrypt (currently plain text)
- JWT tokens stored in localStorage (consider httpOnly cookies for production)
- Implement token refresh mechanism for production

## 📖 Documentation

- [Architecture Overview](ARCHITECTURE.md) - System design and components
- [Implementation Summary](IMPLEMENTATION_SUMMARY.md) - Feature implementation details
- [OAuth2 Setup Guide](OAUTH2_SETUP.md) - OAuth provider configuration
- [Frontend README](em-app-ui/README_UI.md) - Angular app details
- [Security Summary](SECURITY_SUMMARY.md) - Security implementation
- [SonarQube Analysis](SONARQUBE_ANALYSIS_REPORT.md) - Code quality report

## 🛠️ Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Frontend** | Angular, Angular Material, RxJS | 21.1.3 |
| **Backend** | Spring Boot, Spring Security | 4.0.1 |
| **Language** | TypeScript, Java | 5.9.2, 21 |
| **ORM** | JPA/Hibernate, MySQL Connector | - |
| **Security** | Spring OAuth2, JJWT | 0.12.6 |
| **Build Tools** | Maven, npm, Angular CLI | 3.9+, 11.6.2+ |
| **API Docs** | SpringDoc OpenAPI | Latest |

## 🧪 Testing

```bash
# Backend tests
cd em-app-as
mvn test

# Frontend tests
cd em-app-ui
npm test
```

## 🚢 Production Deployment

Before deploying to production:
1. ✅ Hash passwords using BCryptPasswordEncoder
2. ✅ Use httpOnly cookies for JWT storage
3. ✅ Configure CORS for production domains
4. ✅ Enable HTTPS/TLS
5. ✅ Implement token refresh mechanism
6. ✅ Set up proper logging and monitoring
7. ✅ Configure production OAuth2 credentials
8. ✅ Implement rate limiting

## 📝 License

Copyright © 2026 Elite Pro Service Consulting
URL: https://www.eliteproservice-consulting.ca

## 👥 Development Team

See individual module README files for module-specific development instructions.

---

**Last Updated**: February 2026  
**Project Status**: Active Development
