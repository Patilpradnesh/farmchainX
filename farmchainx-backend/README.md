# 🌾 FarmChainX Backend - Enterprise Edition

> **Blockchain-based Agricultural Supply Chain Management System**

[![Version](https://img.shields.io/badge/version-2.0.0-blue.svg)](https://github.com/farmchainx/backend)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

FarmChainX is a production-ready, enterprise-grade supply chain operating system that manages crop provenance, trading, logistics, payments, governance, and consumer trust from farm to fork.

---

## 📋 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Quick Start](#-quick-start)
- [API Documentation](#-api-documentation)
- [Configuration](#-configuration)
- [Database Setup](#-database-setup)
- [Deployment](#-deployment)
- [Development](#-development)
- [Contributing](#-contributing)

---

## ✨ Features

### 🔐 **Authentication & Authorization**
- JWT-based stateless authentication
- Role-based access control (5 roles: ADMIN, FARMER, DISTRIBUTOR, RETAILER, CONSUMER)
- BCrypt password encryption
- Secure API endpoints

### 🌾 **Complete Crop Lifecycle Management**
- Crop registration with blockchain recording
- 6-state lifecycle tracking (CREATED → LISTED → ORDERED → SHIPPED → DELIVERED → CLOSED)
- Ownership transfer management
- Complete audit trail

### 🚚 **Advanced Logistics & Shipment Tracking**
- Real-time GPS location tracking
- IoT condition monitoring (temperature, humidity)
- Vehicle and driver assignment
- QR code generation for packages
- Delivery confirmation workflow

### 🔗 **Blockchain Integration**
- Immutable crop provenance recording
- Smart contract integration framework
- Public verification API
- Ownership transfer on blockchain
- Ethereum network support (Sepolia/Mainnet)

### 📱 **QR Code Generation**
- Crop traceability QR codes
- Shipment tracking QR codes
- Consumer-facing trace URLs
- Base64 encoded for easy embedding

### 📊 **Analytics & Reporting**
- Role-specific dashboards
- Real-time system metrics
- User and crop statistics
- Order and shipment analytics
- CSV export capabilities

### ⚖️ **Dispute Resolution**
- User-initiated dispute creation
- Admin moderation workflow
- Complete dispute history
- Resolution tracking

### 🛡️ **Enterprise Features**
- Complete audit trail
- Standardized API responses
- Comprehensive error handling
- Swagger/OpenAPI documentation
- Health monitoring endpoints

---

## 🏗️ Architecture

### **Technology Stack**

| Layer | Technology |
|-------|------------|
| **Framework** | Spring Boot 3.2.5 |
| **Language** | Java 17 |
| **Security** | Spring Security + JWT |
| **Database** | MySQL 8.0+ |
| **ORM** | JPA/Hibernate |
| **Documentation** | Swagger/OpenAPI |
| **QR Generation** | ZXing |
| **Blockchain** | Web3j (ready) |

### **System Architecture**

```
┌─────────────────────────────────────────────────────┐
│              FarmChainX Backend                     │
│              Spring Boot 3.2.5                      │
└─────────────────────────────────────────────────────┘
                        │
        ┌───────────────┼───────────────┐
        │               │               │
   ┌────▼────┐    ┌────▼────┐    ┌────▼────┐
   │Security │    │   API   │    │  Data   │
   │  Layer  │    │  Layer  │    │  Layer  │
   └─────────┘    └─────────┘    └─────────┘
        │               │               │
   ┌────▼────────────────▼───────────────▼────┐
   │         Business Logic Layer             │
   │  ┌──────────────────────────────────┐   │
   │  │ Auth │ Crop │ Order │ Shipment │  │
   │  │ Blockchain │ Analytics │ Admin │  │
   │  └──────────────────────────────────┘   │
   └──────────────────────────────────────────┘
```

### **Module Structure**

```
com.farmchainx.backend/
├── common/              # Shared utilities
│   ├── dto/            # ApiResponse wrapper
│   ├── exception/      # Custom exceptions
│   └── service/        # QRCodeService
├── blockchain/         # Blockchain integration
├── shipment/          # Logistics tracking
├── config/            # Configuration classes
├── controller/        # REST controllers
├── service/           # Business logic
├── repository/        # Data access
├── entity/            # Database entities
├── dto/               # Data transfer objects
└── enums/             # Enumerations
```

---

## 🚀 Quick Start

### **Prerequisites**

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Git

### **Installation**

1. **Clone the repository**
```bash
git clone https://github.com/farmchainx/backend.git
cd farmchainx-backend
```

2. **Configure database**
```bash
mysql -u root -p
CREATE DATABASE farmchainx_db;
```

3. **Update application.properties**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/farmchainx_db
spring.datasource.username=root
spring.datasource.password=your_password
```

4. **Build and run**
```bash
mvn clean install
mvn spring-boot:run
```

5. **Access the application**
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health Check: http://localhost:8080/health

---

## 📚 API Documentation

### **Swagger UI**

Interactive API documentation is available at:
```
http://localhost:8080/swagger-ui.html
```

### **Key API Endpoints**

#### **Authentication**
```http
POST   /api/auth/register        # User registration
POST   /api/auth/login           # User login
GET    /api/auth/me              # Current user info
```

#### **Crop Management**
```http
POST   /api/v1/crops/register    # Register new crop
GET    /api/v1/crops/trace/{hash} # Trace crop by blockchain hash
```

#### **Shipment Tracking**
```http
POST   /api/v1/shipments                    # Create shipment
GET    /api/v1/shipments/track/{trackingNo} # Track shipment (Public)
PUT    /api/v1/shipments/{id}/location      # Update GPS location
PUT    /api/v1/shipments/{id}/condition     # Update IoT conditions
```

#### **Blockchain**
```http
POST   /api/v1/blockchain/register   # Register on blockchain
GET    /api/v1/blockchain/verify/{tx} # Verify transaction (Public)
```

#### **Admin**
```http
GET    /api/admin/users            # List all users
POST   /api/admin/approve/{userId} # Approve user
GET    /api/v1/reports/system-overview # System analytics
```

### **API Response Format**

All endpoints return standardized responses:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2026-02-10T12:00:00Z",
  "errorCode": null
}
```

---

## ⚙️ Configuration

### **Application Properties**

#### **Database**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/farmchainx_db
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
```

#### **Security**
```properties
jwt.secret=your-secret-key-minimum-32-characters
jwt.expiration=86400000
```

#### **Blockchain**
```properties
blockchain.enabled=false
blockchain.network=sepolia
blockchain.contract.address=0x...
```

#### **Frontend Integration**
```properties
frontend.url=http://localhost:3000
```

### **Environment Profiles**

Create profile-specific properties:
- `application-dev.properties` - Development
- `application-prod.properties` - Production

Run with profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## 🗄️ Database Setup

### **Automated Setup** (Recommended)

The application will auto-create tables on first run:
```properties
spring.jpa.hibernate.ddl-auto=update
```

### **Manual Setup**

Use the provided SQL script:
```bash
mysql -u root -p farmchainx_db < database_reset_commands.sql
```

### **Test Data**

Default test users created on startup:
- **Admin**: admin@farmchainx.com / admin123
- **Farmer**: farmer@farmchainx.com / farmer123
- **Distributor**: distributor@farmchainx.com / distributor123
- **Retailer**: retailer@farmchainx.com / retailer123
- **Consumer**: consumer@farmchainx.com / consumer123

---

## 🚀 Deployment

### **Production Checklist**

- [ ] Update `jwt.secret` to strong random key
- [ ] Set `spring.jpa.hibernate.ddl-auto=validate`
- [ ] Configure production database
- [ ] Enable HTTPS
- [ ] Set up environment variables for secrets
- [ ] Configure logging levels
- [ ] Set up monitoring (Actuator)
- [ ] Enable blockchain integration if needed

### **Docker Deployment**

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/farmchainx-backend.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

Build and run:
```bash
mvn clean package
docker build -t farmchainx-backend .
docker run -p 8080:8080 farmchainx-backend
```

### **Docker Compose**

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: farmchainx_db
      MYSQL_ROOT_PASSWORD: root
    ports:
      - "3306:3306"
  
  backend:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/farmchainx_db
```

---

## 🔧 Development

### **Running Tests**

```bash
mvn test
```

### **Code Style**

Follow standard Java conventions and Spring Boot best practices.

### **API Testing**

Use Swagger UI or tools like Postman:

**Example: Register Crop**
```bash
curl -X POST http://localhost:8080/api/v1/crops/register \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "cropName": "Organic Wheat",
    "quantity": 1000,
    "location": "Maharashtra, India",
    "harvestDate": "2026-02-01"
  }'
```

### **Logging**

Configure logging in `application.properties`:
```properties
logging.level.com.farmchainx=DEBUG
logging.level.org.springframework.web=INFO
```

---

## 📖 Additional Documentation

- **[Complete Project Analysis](PROJECT_COMPLETE_ANALYSIS.md)** - Detailed feature list
- **[System Architecture Map](SYSTEM_ARCHITECTURE_MAP.md)** - Architecture diagrams
- **[Backend Audit Report](BACKEND_AUDIT_REPORT.md)** - Security and quality audit
- **[Enterprise Upgrade Summary](ENTERPRISE_UPGRADE_SUMMARY.md)** - Latest improvements
- **[Database Setup Guide](DATABASE_SETUP_GUIDE.md)** - Database configuration
- **[Integration Guide](COMPLETE_INTEGRATION_GUIDE.md)** - Frontend integration

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

---

## 👥 Team

- **Project Lead**: FarmChainX Development Team
- **Backend Lead**: Enterprise Backend Agent
- **Contact**: support@farmchainx.com

---

## 🙏 Acknowledgments

- Spring Boot community
- ZXing library for QR code generation
- Web3j for blockchain integration
- Swagger/OpenAPI for documentation

---

## 📞 Support

For support and questions:
- **Email**: support@farmchainx.com
- **Documentation**: http://localhost:8080/swagger-ui.html
- **Issues**: GitHub Issues

---

**Built with ❤️ for sustainable agriculture and supply chain transparency**

🌾 **FarmChainX** - *From Farm to Fork, Trust Every Step*
