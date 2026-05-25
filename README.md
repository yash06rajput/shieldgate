# ShieldGate 🛡️

Enterprise-grade API Security Gateway built with Spring Boot, Redis, MySQL, React, and Tailwind CSS.

ShieldGate demonstrates real-world API protection patterns including API key authentication, quota enforcement, Redis-backed distributed rate limiting, burst attack detection, threat logging, and an interactive monitoring dashboard.

---

## Features

### API Key Management
- Generate secure API keys
- Per-key request quota allocation
- Enable / disable API keys
- Delete compromised keys
- Track request usage per key

### Authentication & Authorization
- API key based access control
- Protected vendor endpoints
- Invalid key rejection
- Disabled key blocking

### Rate Limiting
Redis-backed distributed rate limiting:

- Sliding/fixed window request control
- Automatic 429 responses
- Cooldown enforcement
- Multi-request stress test simulation

### Burst Attack Detection
Detect suspicious traffic spikes:

- Abnormal request burst identification
- Critical threat logging
- Security event persistence

### Threat Monitoring Dashboard
Live dashboard showing:

- Threat event feed
- Severity levels
- Endpoint attacked
- Source IP
- Threat message
- API key analytics

### Interactive API Playground
Recruiter/demo-friendly testing environment:

- Single API request simulation
- Parallel stress test simulation
- Success / rate limit / failure classification
- Real-time Redis cooldown visibility

---

## Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Maven

### Database
- MySQL

### Distributed Security Layer
- Redis

### Frontend
- React
- Vite
- Tailwind CSS
- JavaScript

### DevOps
- Git
- GitHub
- Docker (deployment ready)

---

## Architecture

Client
↓
React Frontend (Dashboard + Playground)
↓
Spring Boot API Gateway
↓
Security Filters
├── API Key Authentication
├── Redis Rate Limiter
├── Burst Detector
↓
Business Controllers
↓
MySQL Persistence

---

## Security Flow

Request enters gateway:

1. API key extracted from request header
2. API key validated against database
3. Disabled/invalid keys rejected
4. Redis rate limiter checks quota
5. Burst detector analyzes request frequency
6. Threat event logged if suspicious
7. Valid request reaches protected endpoint
8. Dashboard reflects analytics

---

## Demo Scenarios

### Valid API Key
Expected:
- HTTP 200
- Access granted

### Invalid API Key
Expected:
- HTTP 401
- Authentication failure

### Disabled API Key
Expected:
- HTTP 403 / rejection

### Quota Exhaustion
Expected:
- HTTP 429
- Rate limit exceeded

### Burst Attack
Expected:
- Critical suspicious burst detection log

---

## Local Setup

### Clone

```bash
git clone https://github.com/yash06rajput/shieldgate.git
cd shieldgate
```

### Backend

Configure MySQL + Redis.

Update:

```properties
src/main/resources/application.properties
```

Then:

```bash
./mvnw spring-boot:run
```

### Frontend

```bash
cd shieldgate-frontend
npm install
npm run dev
```

---

## API Endpoints

### API Key Management

```http
POST /api/keys/create
GET /api/keys
PUT /api/keys/{id}/disable
DELETE /api/keys/{id}
```

### Protected Vendor API

```http
GET /api/vendor/test
```

### Threat Monitoring

```http
GET /api/security/events
```

---

## Resume Value

This project demonstrates:

- Backend security engineering
- Distributed systems concepts
- API gateway design
- Redis caching/rate limiting
- Authentication workflows
- Threat monitoring
- Full stack product architecture
- Production-oriented engineering

---

## Future Improvements

- Docker Compose full deployment
- JWT authentication
- Role-based access control
- Prometheus metrics
- Grafana observability
- Kubernetes deployment
- CI/CD pipeline
- Cloud deployment (AWS/GCP)

---

## Author

**Yash Rajput**