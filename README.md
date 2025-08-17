# Notes Shield 🛡️  
A secure note management REST API built with **Spring Boot (Kotlin)**, **JWT Authentication (Access + Refresh Tokens)**, and **MongoDB**.

---

## 📌 Features
- User registration & login with **JWT Access + Refresh Token** authentication
- Secure endpoints with Spring Security
- Role-based access control
- MongoDB for persistent data storage
- CRUD operations for notes
- Global exception handling
- Token validation & expiry handling
- Automatic token refresh without re-login

---

## 🛠️ Tech Stack
- **Backend:** Spring Boot 3, Kotlin
- **Security:** Spring Security, JWT (Access & Refresh)
- **Database:** MongoDB
- **Build Tool:** Gradle
  
---

## ⚙️ Prerequisites
- Java 17+
- Gradle
- MongoDB instance (cloud)
- Environment variables:
  ```env
  MONGO_URI=mongodb://localhost:27017
  EXAMPLE:-
  JWT_SECRET=your_secret_key
  JWT_ACCESS_EXPIRATION=300000        # Access Token: 5 minutes (in ms)
  JWT_REFRESH_EXPIRATION=604800000    # Refresh Token: 7 days (in ms)
