# Compass Platform — Project README

## 🧭 Compass Platform

A full-stack, production-quality travel platform built with **HTML/CSS/JS + Spring Boot + MySQL**.

---

## 🗂 Project Structure

```
travel and tourism/
├── frontend/           ← HTML/CSS/JS web application
│   ├── index.html      ← Home Page  ✅ (Step 1)
│   ├── login.html      ← Auth       (Step 2)
│   ├── planner.html    ← Planner    (Step 3)
│   ├── plan-result.html
│   ├── weather.html
│   ├── emergency.html
│   ├── admin-dashboard.html
│   ├── css/
│   └── js/
│
└── backend/
    └── smarttourism/   ← Spring Boot 3 + MySQL
        ├── pom.xml
        └── src/...
```

---

## 🚀 Running the Application

### Frontend (Step 1)
Open `frontend/index.html` directly in a browser, OR use VS Code Live Server:
```
Right-click index.html → Open with Live Server
```

### Backend (Step 5+)

#### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+

#### Database Setup
```sql
CREATE DATABASE smart_tourism CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### Configuration
Edit `backend/smarttourism/src/main/resources/application.properties`:
```properties
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
```
OR set environment variables:
```
DB_USERNAME=root
DB_PASSWORD=your_password
DB_NAME=smart_tourism
WEATHER_API_KEY=your_openweathermap_api_key
```

#### Run
```bash
cd backend/smarttourism
mvn spring-boot:run
```
Backend starts at: http://localhost:8080

---

## 👤 Demo Credentials (after backend starts)

| Role  | Email                        | Password    |
|-------|------------------------------|-------------|
| Admin | admin@smarttourism.com       | Admin@1234  |
| User  | demo@smarttourism.com        | Demo@1234   |

---

## 📋 Build Progress

| Module | Status |
|--------|--------|
| ✅ Step 1: Project Structure + Home Page | DONE |
| ⬜ Step 2: Login / Register | Pending |
| ⬜ Step 3: Travel Planner | Pending |
| ⬜ Step 4: Recommendations | Pending |
| ⬜ Step 5: Itinerary | Pending |
| ⬜ Step 6: Weather | Pending |
| ⬜ Step 7: Emergency | Pending |
| ⬜ Step 8: Admin Dashboard | Pending |
| ⬜ Step 9: Security/JWT | Pending |
| ⬜ Step 10: Integration Testing | Pending |

---

## ⚠️ Security Notes

- **Never commit** `application-local.properties` or `.env` files
- API keys must be in environment variables
- Passwords are BCrypt-hashed — never stored as plain text
- Emergency GPS is collected only when the user explicitly activates the emergency feature
