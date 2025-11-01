# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.5.7 application for managing a parking lot system. The project uses:
- Java 21
- Maven for dependency management and builds
- PostgreSQL as the database
- Spring Data JPA for database access
- Spring Security for authentication/authorization
- Lombok for reducing boilerplate code

## Build & Development Commands

### Building the Project
```bash
# Clean and build the project
./mvnw clean install

# Build without running tests
./mvnw clean install -DskipTests

# Compile only
./mvnw compile
```

### Running the Application
```bash
# Run the Spring Boot application
./mvnw spring-boot:run

# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Testing
```bash
# Run all tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=ParkingLotApplicationTests

# Run a specific test method
./mvnw test -Dtest=ClassName#methodName

# Run tests with coverage
./mvnw test jacoco:report
```

### Database Setup

The application expects a PostgreSQL database with the following configuration:
- Database name: `parkingLot`
- Port: `5432`
- Username: `postgres`
- Password: (configured in application.properties)

Create the database before running:
```sql
CREATE DATABASE parkingLot;
```

Hibernate will automatically create/update tables based on JPA entities (`spring.jpa.hibernate.ddl-auto=update`).

## Architecture Notes

### Package Structure
The base package is `com.parkingLot`. The project follows standard Spring Boot conventions organized by layers:
- `controllers/` - REST API endpoints
- `services/` - Business logic layer
- `repositories/` - Data access layer (Spring Data JPA)
- `entities/` - JPA entity classes
- `dto/` - Data Transfer Objects (Request/Response objects)
- `security/` - Security configuration and JWT handling
- `config/` - Spring configuration classes
- `exceptions/` - Custom exception classes and global exception handler

### Authentication & Security Architecture

The application implements JWT-based authentication with role-based access control:

1. **JWT Token Flow**: Configured in [JwtUtil.java](src/main/java/com/parkingLot/security/JwtUtil.java)
   - Tokens expire after 6 hours (21600000 ms, configurable via `jwt.expiration`)
   - Secret key is stored in application.properties (`jwt.secret`)
   - Tokens include username (email) and role claims

2. **Security Configuration**: See [SecurityConfig.java](src/main/java/com/parkingLot/config/SecurityConfig.java)
   - `/api/auth/**` endpoints are publicly accessible (login, register)
   - `/api/admin/**` requires `ROLE_ADMIN`
   - `/api/socio/**` requires `ROLE_SOCIO`
   - All other endpoints require authentication
   - CSRF is disabled (stateless JWT authentication)
   - Custom error handling via `CustomAuthenticationEntryPoint` and `CustomAccessDeniedHandler`

3. **User Entity**: [User.java](src/main/java/com/parkingLot/entities/User.java) implements Spring Security's `UserDetails`
   - Username is the email field
   - Users have a single role (ManyToOne relationship with Role entity)
   - Includes audit fields (createdAt, updatedAt) managed by @PrePersist/@PreUpdate

4. **Initial Data**: [DataInitializer.java](src/main/java/com/parkingLot/config/DataInitializer.java) runs on startup
   - Creates ADMIN and SOCIO roles if they don't exist
   - Creates default admin user (admin@mail.com / admin) if not present

### Domain Model

**Core Entities:**
- `User` - System users with authentication details and role
- `Role` - User roles (ADMIN, SOCIO)
- `Parqueadero` - Parking lot entity with capacity, pricing, and owner (socio)

**Key Relationships:**
- User → Role (ManyToOne, EAGER fetch)
- Parqueadero → User/Socio (ManyToOne, EAGER fetch)

### Lombok Configuration
The project uses Lombok extensively. The Maven compiler plugin is configured with annotation processor paths for Lombok. When creating new classes, you can use Lombok annotations like `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`, etc.

### Database Layer
- Use Spring Data JPA repositories by extending `JpaRepository<Entity, ID>`
- Entity classes should be annotated with `@Entity` and placed in the `entities/` package
- Repository interfaces should be placed in the `repositories/` package
- DDL changes are automatically handled by Hibernate (ddl-auto=update)

## Development Workflow

1. **Creating New Features**: Follow Spring Boot layered architecture
   - Controller layer for REST endpoints (in `controllers/`)
   - Service layer for business logic (in `services/`)
   - Repository layer for data access (in `repositories/`)
   - Entity layer for domain objects (in `entities/`)
   - DTOs for request/response objects (in `dto/`)

2. **Adding Secured Endpoints**:
   - Public endpoints: Add to `/api/auth/**` pattern or explicitly permit in SecurityConfig
   - Admin-only endpoints: Use `/api/admin/**` prefix or add `@PreAuthorize("hasRole('ADMIN')")`
   - Socio endpoints: Use `/api/socio/**` prefix or add `@PreAuthorize("hasRole('SOCIO')")`
   - Get authenticated user in controllers via `@AuthenticationPrincipal UserDetails userDetails`

3. **Database Changes**: When adding new entities or modifying existing ones, Hibernate will automatically update the schema on application restart (ddl-auto=update)

4. **Error Handling**: Use custom exceptions (e.g., `BadRequestException`) which are handled globally by [GlobalExceptionHandler.java](src/main/java/com/parkingLot/exceptions/GlobalExceptionHandler.java)

5. **Configuration**: Application settings are in `src/main/resources/application.properties`. For environment-specific configs, use Spring profiles (application-{profile}.properties)

## Security & Configuration Management

### Sensitive Data Protection

**IMPORTANT**: The `application.properties` file is in `.gitignore` to prevent committing sensitive data (database passwords, JWT secrets).

**For local development**, create your own `application.properties` with:
- Database password (`spring.datasource.password`)
- JWT secret key (`jwt.secret`) - Generate a secure 256-bit key

**Best practices for different environments:**

1. **Development**: Use `application-dev.properties` (not committed)
2. **Production**: Use environment variables:
   ```properties
   spring.datasource.password=${DB_PASSWORD}
   jwt.secret=${JWT_SECRET}
   ```

3. **Spring Profiles**: Run with specific profile:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

**Note**: Spring Boot does NOT use `.env` files natively like Node.js. Use environment variables or Spring profiles instead.
