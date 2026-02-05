# EM App - OAuth2 Authentication Implementation

This repository contains a full-stack application with OAuth2 authentication using Spring Boot and Angular Material.

## 🚀 Quick Start

### Prerequisites
- Java 17+ (Note: project configured for Java 21, may need adjustment)
- Node.js 16+
- MySQL 8+
- Maven 3.9+

### 1. Setup Backend

```bash
# Navigate to backend
cd em-app-as

# Set environment variables
export GOOGLE_CLIENT_ID="your-google-client-id"
export GOOGLE_CLIENT_SECRET="your-google-client-secret"
export GITHUB_CLIENT_ID="your-github-client-id"
export GITHUB_CLIENT_SECRET="your-github-client-secret"
export FACEBOOK_CLIENT_ID="your-facebook-app-id"
export FACEBOOK_CLIENT_SECRET="your-facebook-app-secret"
export TIKTOK_CLIENT_ID="your-tiktok-client-key"
export TIKTOK_CLIENT_SECRET="your-tiktok-client-secret"
export JWT_SECRET="your-secret-key-minimum-256-bits"

# Start backend
mvn spring-boot:run
```

Backend runs on: `http://localhost:8080`

### 2. Setup Frontend

```bash
# Navigate to frontend
cd em-app-ui

# Install dependencies
npm install

# Start development server
ng serve
```

Frontend runs on: `http://localhost:4200`

## 📚 Documentation

- **[OAUTH2_SETUP.md](OAUTH2_SETUP.md)** - Comprehensive OAuth2 provider setup guide
- **[ARCHITECTURE.md](ARCHITECTURE.md)** - System architecture and technical details
- **[IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)** - Complete implementation summary
- **[em-app-ui/README.md](em-app-ui/README.md)** - Angular client specific documentation

## 🏗️ Architecture

```
┌─────────────────┐         ┌──────────────────┐         ┌─────────────────┐
│   Angular 16+   │◄───────►│   Spring Boot    │◄───────►│   OAuth2        │
│   Material UI   │   JWT   │   REST API       │  Token  │   Providers     │
└─────────────────┘         └──────────────────┘         └─────────────────┘
                                     │
                                     ▼
                            ┌──────────────────┐
                            │   MySQL 8+       │
                            │   Database       │
                            └──────────────────┘
```

## ✨ Features

### Backend (Spring Boot)
- ✅ OAuth2 authentication with 4 providers (Google, GitHub, Facebook, TikTok)
- ✅ JWT token generation and validation
- ✅ Stateless authentication (no server sessions)
- ✅ CORS configuration for frontend
- ✅ REST API endpoints for user management
- ✅ Security filters and interceptors
- ✅ OpenAPI/Swagger documentation

### Frontend (Angular Material)
- ✅ Modern Material Design UI
- ✅ OAuth2 login with provider selection
- ✅ JWT token management
- ✅ HTTP interceptors for automatic token injection
- ✅ Route guards for protected pages
- ✅ Responsive layout with sidenav
- ✅ Dashboard and management components
- ✅ Error handling and user feedback

## 🔐 Security Features

- **Stateless JWT Authentication**: No server-side sessions
- **HMAC-SHA256 Token Signing**: 256-bit minimum key size
- **Token Expiration**: Configurable (default 24 hours)
- **CORS Protection**: Configured for specific origins
- **Route Protection**: Guards on frontend and backend
- **Error Handling**: Auto-logout on authentication failures

## 📝 Configuration

### Required Environment Variables

Backend (`em-app-as`):
```bash
GOOGLE_CLIENT_ID=...
GOOGLE_CLIENT_SECRET=...
GITHUB_CLIENT_ID=...
GITHUB_CLIENT_SECRET=...
FACEBOOK_CLIENT_ID=...
FACEBOOK_CLIENT_SECRET=...
TIKTOK_CLIENT_ID=...
TIKTOK_CLIENT_SECRET=...
JWT_SECRET=...          # Minimum 256 bits
DB_URL=jdbc:mysql://localhost:3306/media_db
DB_USERNAME=media_db_user
DB_PASSWORD=media_db_pswd
```

Frontend (`em-app-ui/src/environments`):
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'
};
```

## 🧪 Testing

### Backend
```bash
cd em-app-as
mvn test
```

### Frontend
```bash
cd em-app-ui
npm test
```

## 📦 Building for Production

### Backend
```bash
cd em-app-as
mvn clean package
java -jar target/em-app-as-0.0.1-SNAPSHOT.jar
```

### Frontend
```bash
cd em-app-ui
ng build --configuration production
# Deploy dist/ folder to web server
```

## 🔧 OAuth Provider Setup

### Google
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create OAuth 2.0 credentials
3. Set redirect URI: `http://localhost:8080/login/oauth2/code/google`

### GitHub
1. Go to [GitHub Developer Settings](https://github.com/settings/developers)
2. Create new OAuth App
3. Set callback URL: `http://localhost:8080/login/oauth2/code/github`

### Facebook
1. Go to [Facebook Developers](https://developers.facebook.com/)
2. Create new app with Facebook Login
3. Set OAuth redirect URI: `http://localhost:8080/login/oauth2/code/facebook`

### TikTok
1. Go to [TikTok for Developers](https://developers.tiktok.com/)
2. Create new app with Login Kit
3. Set redirect URI: `http://localhost:8080/login/oauth2/code/tiktok`

See [OAUTH2_SETUP.md](OAUTH2_SETUP.md) for detailed instructions.

## 🚨 Security Considerations

### Development
- ✅ Using localhost URLs
- ✅ CORS allows frontend origin
- ✅ Tokens stored in localStorage (convenient for development)

### Production (Recommended Changes)
- ⚠️ Use HTTPS everywhere
- ⚠️ Switch to httpOnly cookies for JWT storage
- ⚠️ Restrict CORS to production domain only
- ⚠️ Add rate limiting
- ⚠️ Implement refresh tokens
- ⚠️ Add audit logging
- ⚠️ Use secret management service (AWS Secrets Manager, etc.)

## 📊 Project Structure

```
em-app/
├── em-app-as/                  # Spring Boot backend
│   ├── src/main/java/
│   │   └── ca/bigmwaj/emapp/as/
│   │       ├── api/           # REST controllers
│   │       │   ├── auth/      # Auth endpoints
│   │       │   └── platform/  # Business APIs
│   │       └── security/      # Security configuration
│   └── src/main/resources/
│       └── application.yml    # App configuration
├── em-app-dm/                 # Data model module
├── em-app-ui/                 # Angular frontend
│   └── src/app/
│       ├── core/              # Core services
│       │   ├── guards/       # Route guards
│       │   ├── interceptors/ # HTTP interceptors
│       │   └── services/     # Auth service
│       ├── features/          # Feature modules
│       │   ├── login/        # Login page
│       │   ├── dashboard/    # Dashboard
│       │   └── ...           # Other features
│       └── shared/           # Shared components
└── docs/                      # Documentation
    ├── OAUTH2_SETUP.md
    ├── ARCHITECTURE.md
    └── IMPLEMENTATION_SUMMARY.md
```

## 🐛 Troubleshooting

### Backend won't start
- Check Java version (needs 17+, configured for 21)
- Verify all environment variables are set
- Ensure MySQL is running and accessible
- Check OAuth credentials are valid

### Frontend can't connect to backend
- Verify backend is running on port 8080
- Check CORS configuration allows localhost:4200
- Ensure no firewall blocking requests

### OAuth login fails
- Verify OAuth credentials are correct
- Check redirect URIs match in provider settings
- Ensure callback URL is accessible
- Check browser console for errors

### JWT token errors
- Verify JWT_SECRET is at least 256 bits
- Check token hasn't expired (default 24 hours)
- Clear localStorage if token is corrupted

## 🤝 Contributing

This is a private project for Elite Maintenance Application. For issues or questions, contact the development team.

## 📄 License

Private - Elite Maintenance Application

## 🙏 Acknowledgments

- Spring Boot and Spring Security teams
- Angular and Angular Material teams
- OAuth2 provider platforms (Google, GitHub, Facebook, TikTok)
