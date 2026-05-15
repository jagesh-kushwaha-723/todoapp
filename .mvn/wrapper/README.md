# TodoApp Backend

A Spring Boot REST API backend for the TodoApp — a full-stack todo list application with JWT authentication, drag and drop reordering, and automatic cleanup of completed tasks.

## Tech Stack

- Java 19
- Spring Boot 3.4.5
- Spring Security + JWT Authentication
- Spring Data JPA
- PostgreSQL
- Lombok

## Features

- User signup and login with JWT authentication
- Create, read, update, delete todos
- Maximum 50 active todos per user
- Completed todos automatically move to the bottom of the list
- Completed todos auto-delete after 30 days
- Drag and drop reordering saved to database
- Database index on user_id for fast queries

## Getting Started

### Prerequisites

- Java 19+
- PostgreSQL 18
- Maven

### Setup

1. Clone the repository
   ```
   git clone https://github.com/YOUR_USERNAME/todoapp.git
   cd todoapp
   ```

2. Create a PostgreSQL database called `todoapp`

3. Copy the config template
   ```
   cp src/main/resources/application-template.yaml src/main/resources/application.yaml
   ```

4. Fill in your database password and JWT secret in `application.yaml`

5. Run the server
   ```
   ./mvnw spring-boot:run
   ```

6. Server runs at `http://localhost:8080`

## API Endpoints

### Auth
| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/auth/signup | Create a new account |
| POST | /api/auth/login | Login and get JWT token |

### Todos
| Method | URL | Description |
|--------|-----|-------------|
| GET | /api/todos | Get all todos for logged in user |
| POST | /api/todos | Create a new todo |
| PUT | /api/todos/{id} | Update a todo |
| DELETE | /api/todos/{id} | Delete a todo |
| PUT | /api/todos/reorder | Save new drag and drop order |

## Project Structure

```
src/main/java/backend/
├── BackendApplication.java     # Entry point
├── HelloController.java        # Test endpoint
├── User.java                   # User entity (maps to users table)
├── Todo.java                   # Todo entity (maps to todos table)
└── (more coming soon)
```

## Environment Variables

See `application-template.yaml` for all required configuration values. Never commit your real `application.yaml` to GitHub.

## Business Rules

- A user can have a maximum of 50 active (not done) todos at a time
- When a todo is marked as done it moves to the bottom of the list
- Completed todos are automatically deleted 30 days after completion
- Users can manually delete any todo at any time
- Drag and drop order is saved permanently to the database

## Author

Built by Jagesh as a full-stack learning project covering React, Spring Boot, REST APIs, JWT authentication, and PostgreSQL.