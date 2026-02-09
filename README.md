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
- Spring Boot 4.0.1 (Java 17)
- OAuth2 authentication (Google, GitHub, Facebook, TikTok)
- JWT token generation and validation
- REST API for Users, Accounts, and Contacts
- MySQL database integration
- Swagger UI documentation
- Performance optimizations:
  - N+1 query prevention with optimized fetch strategies
  - Pagination support (max 1,000 records per query)
  - Efficient entity relationships using SUBSELECT fetch mode

### Frontend
- Angular 21 with TypeScript strict mode
- Angular Material Design components
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
- [SonarQube Analysis](SONARQUBE_ANALYSIS_REPORT.md)

## Performance Considerations

### Database Query Optimization
The application uses several strategies to optimize database performance:

1. **N+1 Query Prevention**: Entity relationships use `@Fetch(FetchMode.SUBSELECT)` to load child collections efficiently
2. **Pagination**: All search endpoints support pagination with a default limit of 1,000 records
3. **Dynamic Query Building**: The `QueryConfig` class provides a flexible DSL for building optimized JPQL queries

### Best Practices
- Use pagination parameters (`pageSize`, `pageIndex`) when fetching large datasets
- Apply filters to reduce result set size before retrieval
- Monitor query execution with Spring Boot Actuator endpoints

## Development

See individual README files in each module for specific development instructions.

## Requirements

- Java 17 or higher
- Node.js 18+ and npm
- MySQL 8.0+
- Maven 3.9+

https://builder.blackbox.ai/
