# OAuth2 Authentication Setup Guide

This guide explains how to configure OAuth2 authentication for the EM App with Google, GitHub, Facebook, and TikTok.

## Architecture Overview

```
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│   Angular   │         │   Spring     │         │   OAuth2    │
│   Client    │◄────────┤   Backend    │◄────────┤   Provider  │
│             │  JWT    │              │  Token  │             │
└─────────────┘         └──────────────┘         └─────────────┘
```

**Flow:**
1. User clicks login button in Angular app
2. Angular redirects to backend `/oauth2/authorization/{provider}`
3. Backend redirects to OAuth provider login page
4. User authenticates with provider
5. Provider redirects back to backend with authorization code
6. Backend exchanges code for access token and user info
7. Backend generates JWT token
8. Backend redirects to Angular callback with JWT token
9. Angular stores token and allows access to protected routes

## Backend Configuration (Spring Boot)

### 1. Add Dependencies

Already added in `em-app-as/pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
```

### 2. Configure OAuth2 Providers

Edit `em-app-as/src/main/resources/application.yml`:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
          github:
            client-id: ${GITHUB_CLIENT_ID}
            client-secret: ${GITHUB_CLIENT_SECRET}
          facebook:
            client-id: ${FACEBOOK_CLIENT_ID}
            client-secret: ${FACEBOOK_CLIENT_SECRET}
          tiktok:
            client-id: ${TIKTOK_CLIENT_ID}
            client-secret: ${TIKTOK_CLIENT_SECRET}

app:
  jwt:
    secret: ${JWT_SECRET}
    expiration: 86400000  # 24 hours
  oauth2:
    redirect-uri: http://localhost:4200/oauth/callback
```

### 3. Security Components

Created files:
- `SecurityConfig.java` - Main security configuration with CORS
- `JwtTokenProvider.java` - JWT token generation and validation
- `JwtAuthenticationFilter.java` - JWT token extraction from requests
- `OAuth2AuthenticationSuccessHandler.java` - Handles successful OAuth2 login
- `AuthController.java` - REST endpoints for auth status

## OAuth2 Provider Setup

### Google OAuth2

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable Google+ API
4. Go to "Credentials" → "Create Credentials" → "OAuth 2.0 Client ID"
5. Application type: Web application
6. Authorized redirect URIs:
   - `http://localhost:8080/login/oauth2/code/google`
7. Copy Client ID and Client Secret
8. Set environment variables:
```bash
export GOOGLE_CLIENT_ID="your-client-id"
export GOOGLE_CLIENT_SECRET="your-client-secret"
```

### GitHub OAuth2

1. Go to [GitHub Developer Settings](https://github.com/settings/developers)
2. Click "New OAuth App"
3. Fill in details:
   - Application name: EM App
   - Homepage URL: `http://localhost:4200`
   - Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`
4. Copy Client ID and generate Client Secret
5. Set environment variables:
```bash
export GITHUB_CLIENT_ID="your-client-id"
export GITHUB_CLIENT_SECRET="your-client-secret"
```

### Facebook OAuth2

1. Go to [Facebook Developers](https://developers.facebook.com/)
2. Create a new app → Consumer
3. Add "Facebook Login" product
4. Settings → Basic:
   - Copy App ID and App Secret
5. Facebook Login → Settings:
   - Valid OAuth Redirect URIs: `http://localhost:8080/login/oauth2/code/facebook`
6. Set environment variables:
```bash
export FACEBOOK_CLIENT_ID="your-app-id"
export FACEBOOK_CLIENT_SECRET="your-app-secret"
```

### TikTok OAuth2

1. Go to [TikTok for Developers](https://developers.tiktok.com/)
2. Create a new app
3. Add Login Kit
4. Copy Client Key and Client Secret
5. Add redirect URI: `http://localhost:8080/login/oauth2/code/tiktok`
6. Set environment variables:
```bash
export TIKTOK_CLIENT_ID="your-client-key"
export TIKTOK_CLIENT_SECRET="your-client-secret"
```

## JWT Configuration

Generate a secure JWT secret (minimum 256 bits):
```bash
export JWT_SECRET="your-very-long-secure-secret-key-at-least-256-bits"
```

Or use:
```bash
export JWT_SECRET=$(openssl rand -base64 32)
```

## Frontend Configuration (Angular)

Already configured in `em-app-ui/src/environments/environment.ts`:
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'
};
```

## Running the Application

### 1. Start Backend

```bash
cd em-app-as

# Set all environment variables
export GOOGLE_CLIENT_ID="..."
export GOOGLE_CLIENT_SECRET="..."
export GITHUB_CLIENT_ID="..."
export GITHUB_CLIENT_SECRET="..."
export FACEBOOK_CLIENT_ID="..."
export FACEBOOK_CLIENT_SECRET="..."
export TIKTOK_CLIENT_ID="..."
export TIKTOK_CLIENT_SECRET="..."
export JWT_SECRET="..."

# Run with Maven
mvn spring-boot:run
```

Backend will start on `http://localhost:8080`

### 2. Start Frontend

```bash
cd em-app-ui
npm install
ng serve
```

Frontend will start on `http://localhost:4200`

## Testing OAuth2 Flow

1. Open `http://localhost:4200` in browser
2. You'll be redirected to login page
3. Click any OAuth provider button (e.g., "Sign in with Google")
4. You'll be redirected to provider's login page
5. After successful login, you'll be redirected back to dashboard
6. JWT token is stored in localStorage
7. All API requests include the token automatically

## Troubleshooting

### Common Issues

**Issue**: CORS errors in browser console
**Solution**: Ensure backend SecurityConfig allows frontend origin:
```java
configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
```

**Issue**: OAuth redirect mismatch
**Solution**: 
- Check redirect URIs match exactly in provider settings
- Backend: `http://localhost:8080/login/oauth2/code/{provider}`
- Frontend callback: `http://localhost:4200/oauth/callback`

**Issue**: JWT token validation fails
**Solution**: 
- Ensure JWT_SECRET is at least 256 bits
- Check token hasn't expired (default: 24 hours)

**Issue**: Provider authentication works but no redirect
**Solution**: Check `OAuth2AuthenticationSuccessHandler` redirect URI configuration

## Security Best Practices

1. **Never commit secrets**: Use environment variables
2. **Use HTTPS in production**: Update all redirect URIs
3. **Rotate JWT secrets regularly**: In production environments
4. **Set appropriate token expiration**: Balance security and UX
5. **Implement refresh tokens**: For long-lived sessions (future enhancement)
6. **Add rate limiting**: Prevent brute force attacks
7. **Log authentication events**: Monitor for suspicious activity

## Production Deployment

### Backend

1. Update `application.yml` for production:
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"

app:
  oauth2:
    redirect-uri: https://your-domain.com/oauth/callback
```

2. Set environment variables securely (e.g., Kubernetes secrets, AWS Parameter Store)

### Frontend

1. Update `environment.prod.ts`:
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://api.your-domain.com'
};
```

2. Build for production:
```bash
ng build --configuration production
```

3. Deploy `dist/` folder to web server or CDN

### OAuth Provider Settings

Update redirect URIs for all providers to use production URLs:
- Backend: `https://api.your-domain.com/login/oauth2/code/{provider}`
- Frontend: `https://your-domain.com/oauth/callback`

## Additional Resources

- [Spring Security OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
- [Angular Authentication](https://angular.io/guide/security)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [OAuth 2.0 Specification](https://oauth.net/2/)
