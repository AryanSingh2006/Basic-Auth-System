# Basic Auth System

**Java:** 17 | **Spring Boot:** 3.5.7

A Spring Boot application with JWT authentication, user registration, login, and logout functionality.

## Postman Collection

Import the API collection: [Open in Postman](https://aryansingh-2720057.postman.co/workspace/chat-app~79d1fa11-f9fd-4849-85d8-9ee5abb4a4ab/collection/46892022-65c1c2b0-afb0-44c3-967b-7b1f5e3f29c7?action=share&creator=46892022&active-environment=46892022-e72590fc-564f-4327-8348-e012114e5cb9)

---

## How to Start the Project

### Windows (PowerShell)
```
.\mvnw.cmd spring-boot:run
```

### macOS / Linux
```
./mvnw spring-boot:run
```

The app will start on `http://localhost:8080`

---

## API Requests

### 1. Register a New User
**POST** `/api/auth/register`

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"password123","role":"USER"}'
```

**Response (201 Created):**
```
User registered successfully
```

---

### 2. Login
**POST** `/api/auth/login`

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"password123"}'
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

Save the token — you'll need it for protected endpoints.

---

### 3. Get Current User (Protected)
**GET** `/api/me`

**Request:**
```bash
curl -X GET http://localhost:8080/api/me \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "alice",
  "password": "...",
  "role": "USER"
}
```

---

### 4. Admin Dashboard (Admin Only)
**GET** `/api/admin/users`

**Request:**
```bash
curl -X GET http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer <admin-jwt-token>"
```

**Response (200 OK):**
```
Welcome Admin admin! This is admin dashboard.
```

> Note: This endpoint requires an ADMIN role.

---

### 5. Logout
**POST** `/api/auth/logout`

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Response (200 OK):**
```
Logged out successfully
```

The token is added to a blacklist and cannot be reused.

---

## Quick Test Flow

1. **Register** a user
2. **Login** to get a token
3. **Use the token** in the Authorization header for protected endpoints
4. **Logout** to invalidate the token
