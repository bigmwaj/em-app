# API Documentation

## Overview

This document describes the REST API endpoints available in the em-app application server.

**Base URL**: `http://localhost:8080`

**Authentication**: Most endpoints require JWT Bearer token authentication.

## Authentication Endpoints

### OAuth2 Login

#### Google Login
```
GET /oauth2/authorization/google
```
Redirects to Google OAuth2 consent page.

#### GitHub Login
```
GET /oauth2/authorization/github
```
Redirects to GitHub OAuth2 consent page.

#### Facebook Login
```
GET /oauth2/authorization/facebook
```
Redirects to Facebook OAuth2 consent page.

#### TikTok Login
```
GET /oauth2/authorization/tiktok
```
Redirects to TikTok OAuth2 consent page.

### OAuth2 Callback
```
GET /login/oauth2/code/{provider}
```
Handles OAuth2 callback from provider and redirects to frontend with JWT token.

**Redirects to**: `http://localhost:4200/oauth/callback?token={jwt_token}`

### Get Current User
```
GET /auth/user
```
Returns the currently authenticated user information.

**Headers**:
- `Authorization: Bearer {jwt_token}`

**Response**:
```json
{
  "email": "user@example.com",
  "name": "John Doe",
  "picture": "https://example.com/photo.jpg",
  "provider": "google"
}
```

### Check Authentication Status
```
GET /auth/status
```
Checks if the current user is authenticated.

**Response**:
```json
{
  "authenticated": true,
  "user": {
    "email": "user@example.com",
    "name": "John Doe"
  }
}
```

## User Management API

Base path: `/api/v1/platform/user`

### Search Users
```
GET /api/v1/platform/user
```
Retrieves a paginated list of users with optional filtering and sorting.

**Headers**:
- `Authorization: Bearer {jwt_token}`

**Query Parameters**:
- `pageSize` (optional): Number of results per page (positive integer)
- `pageIndex` (optional): Page number (positive integer)
- `calculateStatTotal` (optional): Calculate total count (boolean)
- `filters` (optional): Filter criteria (see filtering documentation)
- `sortBy` (optional): Sort criteria (see sorting documentation)

**Response**:
```json
{
  "data": [
    {
      "id": 1,
      "username": "johndoe",
      "email": "john@example.com",
      "name": "John Doe",
      "status": "ACTIVE",
      "provider": "google",
      "picture": "https://example.com/photo.jpg",
      "contact": {
        "id": 1,
        "firstName": "John",
        "lastName": "Doe",
        "email": "john@example.com",
        "phone": "+1234567890",
        "company": "Acme Corp"
      }
    }
  ],
  "total": 1,
  "pageSize": 30,
  "pageIndex": 0
}
```

### Get User by ID
```
GET /api/v1/platform/user/user-id/{userId}
```
Retrieves a specific user by ID.

**Headers**:
- `Authorization: Bearer {jwt_token}`

**Path Parameters**:
- `userId`: User ID (positive integer, required)

**Response**:
```json
{
  "message": "Success",
  "data": {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "name": "John Doe",
    "status": "ACTIVE",
    "contact": { ... }
  }
}
```

### Create User
```
POST /api/v1/platform/user
```
Creates a new user.

**Headers**:
- `Authorization: Bearer {jwt_token}`
- `Content-Type: application/json`

**Request Body**:
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "name": "John Doe",
  "status": "ACTIVE",
  "contact": {
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "phone": "+1234567890",
    "company": "Acme Corp"
  }
}
```

**Response**:
```json
{
  "message": "Success",
  "data": {
    "id": 1,
    "username": "johndoe",
    ...
  }
}
```

### Update User
```
PATCH /api/v1/platform/user
```
Updates an existing user.

**Headers**:
- `Authorization: Bearer {jwt_token}`
- `Content-Type: application/json`

**Request Body**:
```json
{
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "name": "John Doe Updated",
  "status": "ACTIVE",
  "contact": { ... }
}
```

**Response**:
```json
{
  "message": "Success",
  "data": {
    "id": 1,
    "username": "johndoe",
    "name": "John Doe Updated",
    ...
  }
}
```

### Delete User
```
DELETE /api/v1/platform/user/user-id/{userId}
```
Deletes a user by ID.

**Headers**:
- `Authorization: Bearer {jwt_token}`

**Path Parameters**:
- `userId`: User ID (positive integer, required)

**Response**:
```json
{
  "message": "User deleted successfully"
}
```

## Account Management API

Base path: `/api/v1/platform/account`

### Search Accounts
```
GET /api/v1/platform/account
```
Retrieves a paginated list of accounts with optional filtering and sorting.

**Query Parameters**: Similar to User Search

**Response**:
```json
{
  "data": [
    {
      "id": 1,
      "name": "Acme Corporation",
      "type": "BUSINESS",
      "status": "ACTIVE",
      "contacts": [],
      "createdDate": "2026-01-15T10:30:00Z"
    }
  ],
  "total": 1,
  "pageSize": 30,
  "pageIndex": 0
}
```

### Get Account by ID
```
GET /api/v1/platform/account/account-id/{accountId}
```
Retrieves a specific account by ID.

### Create Account
```
POST /api/v1/platform/account
```
Creates a new account.

**Request Body**:
```json
{
  "name": "Acme Corporation",
  "type": "BUSINESS",
  "status": "ACTIVE"
}
```

**Account Types**:
- `BUSINESS`
- `PERSONAL`
- `GOVERNMENT`
- `NON_PROFIT`

### Update Account
```
PATCH /api/v1/platform/account
```
Updates an existing account.

### Delete Account
```
DELETE /api/v1/platform/account/account-id/{accountId}
```
Deletes an account by ID.

## Contact Management API

Base path: `/api/v1/platform/contact`

### Search Contacts
```
GET /api/v1/platform/contact
```
Retrieves a paginated list of contacts.

**Response**:
```json
{
  "data": [
    {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john@example.com",
      "phone": "+1234567890",
      "company": "Acme Corp",
      "createdDate": "2026-01-15T10:30:00Z"
    }
  ],
  "total": 1
}
```

### Get Contact by ID
```
GET /api/v1/platform/contact/contact-id/{contactId}
```

### Create Contact
```
POST /api/v1/platform/contact
```

**Request Body**:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "phone": "+1234567890",
  "company": "Acme Corp"
}
```

### Update Contact
```
PATCH /api/v1/platform/contact
```

### Delete Contact
```
DELETE /api/v1/platform/contact/contact-id/{contactId}
```

## Status Codes

- `200 OK`: Request successful
- `201 Created`: Resource created successfully
- `400 Bad Request`: Invalid request parameters or body
- `401 Unauthorized`: Missing or invalid JWT token
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

## Error Response Format

```json
{
  "timestamp": "2026-02-07T17:10:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/platform/user"
}
```

## Filtering and Sorting

### Supported User Filters
- `status`: User status (ACTIVE, INACTIVE, PENDING)
- `username`: Username (string)
- `firstName`: First name (string)
- `lastName`: Last name (string)
- `phone`: Phone number (string)
- `email`: Email address (string)
- `address`: Address (string)

### Supported User Sort Fields
- `status`
- `username`
- `firstName`
- `lastName`
- `phone`
- `email`
- `address`

### Filter Syntax
Filters use Base64 encoded JSON format. Example:
```json
[
  {
    "field": "status",
    "operator": "eq",
    "value": "ACTIVE"
  }
]
```

### Sort Syntax
```
sortBy=field1:asc,field2:desc
```

## Swagger UI

Interactive API documentation is available at:
```
http://localhost:8080/swagger-ui/index.html
```

This provides a web interface to explore and test all API endpoints.
