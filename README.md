# VBooking Hotel - Backend Application

Spring Boot backend for hotel booking system.

## Setup Instructions

### 1. Database Configuration

Create a SQL Server database and update the connection details:

```bash
# Copy the example properties file
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Then edit `application.properties` with your actual values:
- Database URL, username, password
- JWT secret key (generate a secure random key)
- Gmail SMTP credentials (if using email features)

### 2. Gmail SMTP Setup (Optional)

To enable email sending:
1. Go to https://myaccount.google.com/apppasswords
2. Generate an App Password
3. Update `application.properties`:
   ```properties
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-app-password
   app.email.from=your-email@gmail.com
   ```

### 3. Run Application

```bash
mvn spring-boot:run
```

The application will start on port **8181**.

## Security Notes

⚠️ **IMPORTANT**: Never commit `application.properties` with real credentials!
- The file is already added to `.gitignore`
- Use `application.properties.example` as a template
- Set production secrets via environment variables

## API Documentation

Base URL: `http://localhost:8181/api/v1`

### Endpoints
- Authentication: `/api/v1/auth/*`
- Hotels: `/api/v1/hotels/*`
- Bookings: `/api/v1/bookings/*`
- Users: `/api/v1/users/*`

## Database Migrations

This project uses Flyway for database versioning. Migrations are located in:
```
src/main/resources/db/migration/
```

## Technologies
- Spring Boot 3.x
- Spring Security + JWT
- SQL Server
- Flyway Migration
- JavaMailSender (Gmail SMTP)
