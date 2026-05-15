# TodoApp Backend

A fully functional REST API backend for TodoApp — a full-stack todo list application built with Java and Spring Boot. This is a learning project covering REST APIs, JWT authentication, Spring Security, JPA, and PostgreSQL.

---

## Tech Stack

| Technology | Purpose |
|------------|---------|
| Java 19 | Programming language |
| Spring Boot 3.4.5 | Backend framework |
| Spring Security | Route protection |
| JWT (jjwt 0.12.3) | Authentication tokens |
| Spring Data JPA | Database communication |
| Hibernate | ORM (Object Relational Mapping) |
| PostgreSQL 18 | Database |
| Lombok | Reduces boilerplate code |
| Maven | Dependency management |

---

## Features

- User signup with BCrypt password hashing (passwords never stored as plain text)
- User login returning a JWT token valid for 24 hours
- All todo routes protected — JWT token required
- Create, read, update, delete todos
- Maximum 50 active todos per user (enforced in backend)
- Mark todos as done — automatically moves to bottom of list
- Uncheck a todo — moves back to bottom of active list
- Drag and drop reordering saved permanently to database
- Completed todos auto-delete after 30 days (scheduler runs daily at midnight)
- Users can manually delete any todo at any time
- Database index on user_id for fast queries

---

## Project Structure

```
src/main/java/backend/
│
├── BackendApplication.java          # Entry point, enables scheduling
│
├── controller/
│   ├── AuthController.java          # POST /api/auth/signup, /api/auth/login
│   ├── TodoController.java          # All /api/todos/** endpoints
│   └── HelloController.java         # GET /hello (test endpoint)
│
├── service/
│   ├── AuthService.java             # Signup and login business logic
│   ├── TodoService.java             # Todo business logic + scheduler
│   └── JwtService.java              # Generate, validate, read JWT tokens
│
├── repository/
│   ├── UserRepository.java          # Database queries for users
│   └── TodoRepository.java          # Database queries for todos
│
├── model/
│   ├── User.java                    # User entity (maps to users table)
│   └── Todo.java                    # Todo entity (maps to todos table)
│
├── dto/
│   ├── SignupRequest.java           # Incoming signup data
│   ├── LoginRequest.java            # Incoming login data
│   ├── LoginResponse.java           # Outgoing token + user info
│   ├── TodoRequest.java             # Incoming todo create/update data
│   └── TodoResponse.java            # Outgoing todo data to frontend
│
└── config/
    ├── SecurityConfig.java          # Spring Security rules
    └── JwtAuthFilter.java           # Intercepts requests, validates JWT
```

---

## Database Design

### users table
| Column | Type | Notes |
|--------|------|-------|
| id | bigint | Auto-increment primary key |
| name | varchar(255) | Not null |
| email | varchar(255) | Not null, unique |
| password | varchar(255) | Not null, BCrypt hashed |
| created_at | timestamp | Set automatically on save |

### todos table
| Column | Type | Notes |
|--------|------|-------|
| id | bigint | Auto-increment primary key |
| title | varchar(255) | Not null |
| done | boolean | Default false |
| order_index | integer | Position for drag and drop ordering |
| completed_at | timestamp | Set when marked done, cleared when unchecked |
| created_at | timestamp | Set automatically on save |
| user_id | bigint | Foreign key to users.id |

**Index:** `idx_todos_user_id` on `todos(user_id)` for fast per-user queries

Tables are auto-created by Spring Boot JPA on startup — no manual SQL needed.

---

## API Endpoints

### Auth (public — no token needed)

| Method | URL | Body | Response |
|--------|-----|------|----------|
| POST | `/api/auth/signup` | `{ name, email, password }` | `"Signup successful"` |
| POST | `/api/auth/login` | `{ email, password }` | `{ token, name, email }` |

### Todos (protected — JWT token required)

| Method | URL | Body | Response |
|--------|-----|------|----------|
| GET | `/api/todos` | — | List of todos (active first, completed at bottom) |
| POST | `/api/todos` | `{ title }` | Created todo |
| PUT | `/api/todos/{id}` | `{ title?, done? }` | Updated todo |
| DELETE | `/api/todos/{id}` | — | 204 No Content |
| PUT | `/api/todos/reorder` | `[1, 3, 2, 5]` ordered list of todo IDs | 200 OK |

### Sending the JWT token

Add this header to every protected request:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## Business Rules

- Maximum **50 active** (not done) todos per user — enforced in TodoService
- Marking a todo done sets `completedAt` and moves it to bottom of list
- Unchecking a todo clears `completedAt` and moves it to bottom of active list
- Completed todos older than 30 days are auto-deleted daily at midnight
- Every todo operation verifies the todo belongs to the requesting user

---

## How JWT Authentication Works

```
1. User logs in → server verifies password
2. Server generates a JWT token signed with a secret key
3. Token is returned to the client
4. Client sends token in Authorization header with every request
5. JwtAuthFilter intercepts every request and validates the token
6. If valid → request proceeds, user email extracted from token
7. If invalid or missing → 403 Forbidden
```

---

## Getting Started

### Prerequisites

- Java 19+
- PostgreSQL 18
- Maven (included via ./mvnw)

### Setup

1. Clone the repository
   ```bash
   git clone https://github.com/jagesh-kushwaha-723/todoapp.git
   cd todoapp
   ```

2. Create a PostgreSQL database
   ```sql
   CREATE DATABASE todoapp;
   ```

3. Copy the config template
   ```bash
   cp src/main/resources/application-template.yaml src/main/resources/application.yaml
   ```

4. Fill in your values in `application.yaml`
   ```yaml
   spring:
     datasource:
       password: YOUR_POSTGRES_PASSWORD
   jwt:
     secret: YOUR_SECRET_KEY_MIN_32_CHARACTERS
   ```

5. Run the server
   ```bash
   ./mvnw spring-boot:run
   ```

6. Server starts at `http://localhost:8080`

---

## Testing the API

Use Postman to test all endpoints.

**Signup:**
```json
POST http://localhost:8080/api/auth/signup
{
    "name": "Your Name",
    "email": "you@email.com",
    "password": "yourpassword"
}
```

**Login:**
```json
POST http://localhost:8080/api/auth/login
{
    "email": "you@email.com",
    "password": "yourpassword"
}
```

**Create a todo (add Bearer token in Authorization header):**
```json
POST http://localhost:8080/api/todos
{
    "title": "Buy groceries"
}
```

---

## Environment Variables

See `application-template.yaml` for all required configuration. Never commit your real `application.yaml` — it is gitignored.

---

## Deployment (Planned)

| Service | Purpose |
|---------|---------|
| Render | Host Spring Boot backend (free tier) |
| Render / Railway | Host PostgreSQL database (free tier) |
| Vercel | Host React frontend (free tier) |

---

## What's Next

- [ ] React frontend (Vite + React Router + Axios + dnd-kit)
- [ ] Connect frontend to this backend
- [ ] Deploy everything for free

---

## Author

Built by Jagesh as a full-stack learning project. Every concept — HTTP, REST APIs, JWT authentication, Spring Boot, PostgreSQL, JPA, and React — learned by building this app from scratch.