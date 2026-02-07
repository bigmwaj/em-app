# smart-cm-project

Smart Community Manager Project

This is a full-stack application with:
- **Backend**: Spring Boot REST API with OAuth2 authentication
- **Frontend**: Angular Material web application with JWT authentication

## Project Structure

```
em-app/
├── em-app-as/          # Application Server (Spring Boot)
├── em-app-dm/          # Data Model (JPA Entities)
├── em-app-ui/          # Angular Frontend Application
├── local-infra/        # Local infrastructure setup
└── docker-compose.yml  # Docker compose configuration
```

## Quick Start

### Backend (Spring Boot)

```bash
# Build the project
mvn clean install

# Run the application server
cd em-app-as
mvn spring-boot:run
```

Backend will be available at `http://localhost:8080`

### Frontend (Angular)

```bash
# Navigate to the UI directory
cd em-app-ui

# Install dependencies
npm install

# Start development server
npm start
```

Frontend will be available at `http://localhost:4200`

## Features

### Backend
- Spring Boot 4.0.1
- OAuth2 authentication (Google, GitHub, Facebook, TikTok)
- JWT token generation and validation
- REST API for Users, Accounts, and Contacts with full CRUD operations
- MySQL database integration
- Swagger UI documentation

### Frontend
- Angular 21 with TypeScript strict mode
- Angular Material Design components
- **Full CRUD functionality** for Users, Accounts, and Contacts
  - Create entities with form validation
  - Edit entities with pre-filled forms
  - Delete entities with confirmation dialogs
  - Real-time notifications for all operations
- JWT authentication with HTTP interceptors
- Route guards for protected pages
- Responsive layout (mobile + desktop)
- OAuth 2.0 login integration

## Documentation

- [Architecture Overview](ARCHITECTURE.md)
- [Implementation Summary](IMPLEMENTATION_SUMMARY.md)
- [OAuth2 Setup Guide](OAUTH2_SETUP.md)
- [Frontend README](em-app-ui/README_UI.md)
- [Security Summary](SECURITY_SUMMARY.md)

## Development

See individual README files in each module for specific development instructions.

https://builder.blackbox.ai/
