# Finance Dashboard Backend

A RESTful backend for a finance dashboard system with **Role-Based Access Control (RBAC)**, built with **Java 17**, **Spring Boot 3**, **Spring Security**, **JWT Authentication**, and **MySQL**.

---

## Table of Contents
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Setup & Running](#setup--running)
- [Authentication](#authentication)
- [Roles & Permissions](#roles--permissions)
- [API Reference](#api-reference)
- [Assumptions & Design Decisions](#assumptions--design-decisions)

---

## Tech Stack

| Layer        | Technology                      |
|--------------|---------------------------------|
| Language     | Java 17                         |
| Framework    | Spring Boot 3.2.5               |
| Security     | Spring Security + JWT (JJWT)    |
| Database     | MySQL                           |
| ORM          | Spring Data JPA / Hibernate     |
| Validation   | Jakarta Bean Validation         |
| Build Tool   | Maven                           |

---

## Project Structure

```
src/main/java/com/finance/
├── config/                  # Security configuration
│   └── SecurityConfig.java
├── controller/              # REST endpoint handlers
│   ├── AuthController.java
│   ├── UserController.java
│   ├── FinancialRecordController.java
│   └── DashboardController.java
├── dto/                     # Request and response objects
│   ├── LoginRequest.java
│   ├── AuthResponse.java
│   ├── UserRequest.java
│   ├── UserResponse.java
│   ├── FinancialRecordRequest.java
│   ├── FinancialRecordResponse.java
│   └── DashboardSummaryResponse.java
├── entity/                  # JPA database models
│   ├── User.java
│   └── FinancialRecord.java
├── enums/
│   ├── Role.java            # VIEWER, ANALYST, ADMIN
│   └── TransactionType.java # INCOME, EXPENSE
├── exception/               # Custom exceptions + global handler
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── BadRequestException.java
├── repository/              # Spring Data JPA repositories
│   ├── UserRepository.java
│   └── FinancialRecordRepository.java
├── security/                # JWT utilities and filter
│   ├── JwtUtils.java
│   ├── JwtAuthFilter.java
│   └── CustomUserDetailsService.java
├── service/                 # Business logic
│   ├── AuthService.java
│   ├── UserService.java
│   ├── FinancialRecordService.java
│   └── DashboardService.java
└── FinanceBackendApplication.java
```

---

## Setup & Running

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8+

### 1. Create the Database

```sql
CREATE DATABASE finance_db;
```

### 2. Configure `application.properties`

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/finance_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

On first startup, a default admin user is automatically created:

```
Email:    admin@finance.com
Password: admin123
```

The server starts at: `http://localhost:8080`

---

## Authentication

This API uses **JWT Bearer Token** authentication.

### Flow:
1. Call `POST /api/auth/login` with credentials
2. Receive a JWT token in the response
3. Include the token in all subsequent requests:

```
Authorization: Bearer <your_token_here>
```

Tokens expire after **24 hours**.

---

## Roles & Permissions

| Action                         | VIEWER | ANALYST | ADMIN |
|--------------------------------|--------|---------|-------|
| Login                          | ✅     | ✅      | ✅    |
| View dashboard summary         | ✅     | ✅      | ✅    |
| View financial records         | ✅     | ✅      | ✅    |
| Create financial records       | ❌     | ✅      | ✅    |
| Update financial records       | ❌     | ✅      | ✅    |
| Delete financial records       | ❌     | ❌      | ✅    |
| View users                     | ❌     | ❌      | ✅    |
| Create / update / delete users | ❌     | ❌      | ✅    |
| Toggle user active status      | ❌     | ❌      | ✅    |

---

## API Reference

### Auth

| Method | Endpoint          | Access  | Description        |
|--------|-------------------|---------|--------------------|
| POST   | /api/auth/login   | Public  | Login, get JWT     |

**Login Request:**
```json
{
  "email": "admin@finance.com",
  "password": "admin123"
}
```
**Login Response:**
```json
{
  "token": "eyJhbGci...",
  "role": "ADMIN",
  "name": "System Admin"
}
```

---

### Users (ADMIN only)

| Method | Endpoint                          | Description               |
|--------|-----------------------------------|---------------------------|
| POST   | /api/users                        | Create a new user         |
| GET    | /api/users                        | Get all users             |
| GET    | /api/users/{id}                   | Get a user by ID          |
| PUT    | /api/users/{id}                   | Update a user             |
| PATCH  | /api/users/{id}/toggle-status     | Activate / deactivate     |
| DELETE | /api/users/{id}                   | Delete a user             |

**Create User Request:**
```json
{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "password": "secure123",
  "role": "ANALYST"
}
```

---

### Financial Records

| Method | Endpoint             | Access           | Description                       |
|--------|----------------------|------------------|-----------------------------------|
| POST   | /api/records         | ANALYST, ADMIN   | Create a record                   |
| GET    | /api/records         | ALL              | Get records (with filters)        |
| GET    | /api/records/{id}    | ALL              | Get a single record               |
| PUT    | /api/records/{id}    | ANALYST, ADMIN   | Update a record                   |
| DELETE | /api/records/{id}    | ADMIN            | Delete a record                   |

**Create Record Request:**
```json
{
  "amount": 5000.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2024-06-01",
  "notes": "Monthly salary for June"
}
```

**Filtering Records (Query Parameters):**
```
GET /api/records?type=EXPENSE
GET /api/records?category=Rent
GET /api/records?startDate=2024-01-01&endDate=2024-06-30
GET /api/records?type=INCOME&startDate=2024-01-01&endDate=2024-12-31
```

---

### Dashboard

| Method | Endpoint                      | Access | Description                |
|--------|-------------------------------|--------|----------------------------|
| GET    | /api/dashboard/summary        | ALL    | Full financial summary     |
| GET    | /api/dashboard/summary?year=2024 | ALL | Summary for a specific year |

**Dashboard Response:**
```json
{
  "totalIncome": 60000.00,
  "totalExpenses": 24000.00,
  "netBalance": 36000.00,
  "incomeByCategory": {
    "Salary": 55000.00,
    "Freelance": 5000.00
  },
  "expenseByCategory": {
    "Rent": 12000.00,
    "Food": 6000.00,
    "Utilities": 6000.00
  },
  "recentTransactions": [...],
  "monthlyIncome": {
    "1": 5000.00,
    "2": 5000.00
  },
  "monthlyExpenses": {
    "1": 2000.00,
    "2": 2000.00
  }
}
```

---

## Assumptions & Design Decisions

1. **Email as username**: The system uses email as the unique identifier for login, which is standard for finance apps.

2. **BigDecimal for amounts**: Financial calculations use `BigDecimal` (not `double`) to prevent floating-point precision errors — a critical requirement for any money-handling system.

3. **BCrypt for passwords**: All passwords are hashed with BCrypt before storage. Plain-text passwords are never persisted.

4. **Stateless sessions**: The API is fully stateless. Each request must include a valid JWT token. No server-side sessions are maintained.

5. **Auto-seeded admin**: On first startup, a default admin user is created so there is always a way to access the system initially. This should be removed or changed in a production environment.

6. **Role hierarchy design**: ANALYST can create and update records (like a data-entry role) but cannot delete them or manage users — only ADMINs have destructive permissions.

7. **Separation of concerns**: Controllers handle HTTP only. Services contain business logic. Repositories handle data access. DTOs are used for all request/response handling — entities are never exposed directly.

8. **Date filtering**: All date-based filters use `LocalDate` (no time component) to keep filtering intuitive and simple for financial records.

9. **Inactive users**: Rather than deleting users, admins can deactivate them. This preserves audit trail and foreign key integrity (records created by them still have a valid creator reference).

10. **`updatedAt` via `@PreUpdate`**: The `updatedAt` field on `FinancialRecord` is automatically updated on every save via a JPA lifecycle hook, removing the need to set it manually in service code.
