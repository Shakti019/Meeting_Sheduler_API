# Meeting Scheduler API - Spring Boot REST API

A complete JWT-authenticated REST API for scheduling meetings using Spring Boot, MongoDB, and JWT tokens.

## Project Structure

```
com/App/live/Domain/
├── Users/                 # User authentication entities
│   ├── Model/
│   │   ├── User.java      # User entity implementing UserDetails
│   │   └── Role.java      # User roles enum (USER, ADMIN)
│   └── Repository/
│       └── UserRepository.java  # MongoDB user queries
│
├── Security/              # Authentication and authorization
│   ├── Config/
│   │   ├── SecurityConfig.java      # Spring Security configuration
│   │   └── ApplicationConfig.java    # Bean definitions
│   ├── Dto/
│   │   ├── AuthRequest.java         # Login payload
│   │   ├── RegisterRequest.java     # Register payload
│   │   └── AuthResponse.java        # JWT token response
│   ├── Service/
│   │   ├── JwtService.java          # JWT token generation/validation
│   │   └── AuthService.java         # Authentication logic
│   ├── Filter/
│   │   └── JwtAuthenticationFilter.java  # JWT request filter
│   └── Controller/
│       └── AuthController.java      # /api/auth endpoints
│
└── Meeting/              # Meeting management features
    ├── Model/
    │   └── Meeting.java  # Meeting entity
    ├── Repository/
    │   └── MeetingRepository.java  # MongoDB meeting queries
    ├── Services/
    │   └── MeetingService.java     # Meeting business logic
    └── Controller/
        └── MeetingController.java  # /api/meetings endpoints
```

## Prerequisites

- **Java 17+**
- **Maven 3.6+**
- **MongoDB 5.0+** (running locally on `localhost:27017`)

## Setup Instructions

### 1. Install MongoDB

Download and install MongoDB Community Server from [mongodb.com](https://www.mongodb.com/try/download/community)

Start MongoDB:
```bash
# Windows
net start MongoDB

# macOS
brew services start mongodb-community

# Linux
sudo systemctl start mongod
```

### 2. Build the Project

```bash
cd live
mvn clean compile -DskipTests
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The API will be available at **`http://localhost:8080`**

## API Endpoints

### Authentication Endpoints (`/api/auth`)

#### 1. Register a New User
```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNzI0MDAwMDAwLCJleHAiOjE3MjQwODY0MDB9.xyz..."
}
```

#### 2. Login
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Meeting Endpoints (`/api/meetings`)

**All meeting endpoints require JWT authentication. Include the token in the `Authorization` header:**
```
Authorization: Bearer <your_jwt_token>
```

#### 1. Create a Meeting
```bash
POST http://localhost:8080/api/meetings
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

{
  "title": "Project Kickoff",
  "description": "Initial planning meeting for the new project",
  "startTime": "2026-04-20T10:00:00",
  "endTime": "2026-04-20T11:00:00"
}
```

#### 2. Get All Meetings
```bash
GET http://localhost:8080/api/meetings
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

#### 3. Get My Meetings (Organized by you)
```bash
GET http://localhost:8080/api/meetings/my-meetings
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

#### 4. Get Meeting by ID
```bash
GET http://localhost:8080/api/meetings/{id}
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

#### 5. Update Meeting
```bash
PUT http://localhost:8080/api/meetings/{id}
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

{
  "title": "Updated Title",
  "description": "Updated description",
  "startTime": "2026-04-20T14:00:00",
  "endTime": "2026-04-20T15:00:00"
}
```

#### 6. Delete Meeting
```bash
DELETE http://localhost:8080/api/meetings/{id}
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## Testing with Postman

1. **Import the endpoints** above into Postman
2. **Set up a variable** for JWT token:
   - In Postman, go to **Tests** tab in the register/login response
   - Add: `pm.environment.set("jwt_token", pm.response.json().token)`
3. **Use the variable** in Authorization headers: `Bearer {{jwt_token}}`

## Security Features

✅ **Password Hashing**: Passwords are hashed using BCrypt  
✅ **JWT Tokens**: Stateless authentication with 24-hour expiration  
✅ **Role-based Access**: USER and ADMIN roles supported  
✅ **CSRF Protection**: Disabled for REST APIs (stateless)  
✅ **CORS Ready**: Can be configured for cross-origin requests  

## Database Schema

### Users Collection
```json
{
  "_id": "ObjectId",
  "name": "string",
  "email": "string (unique)",
  "password": "string (hashed)",
  "role": "USER | ADMIN"
}
```

### Meetings Collection
```json
{
  "_id": "ObjectId",
  "title": "string",
  "description": "string",
  "startTime": "ISODate",
  "endTime": "ISODate",
  "organizerEmail": "string",
  "createdAt": "ISODate",
  "updatedAt": "ISODate"
}
```

## Next Steps

1. ✅ Authentication & Authorization complete
2. 📱 Frontend Integration: Connect with React/Angular frontend
3. 📧 Email Notifications: Send meeting reminders
4. 📅 Calendar Integration: Sync with Google Calendar
5. 🔔 Real-time Updates: Add WebSocket for live meeting updates
6. 📊 Analytics: Track meeting statistics
7. 🗂️ File Storage: Attach documents to meetings

## Troubleshooting

**MongoDB Connection Error:**
- Ensure MongoDB is running: `mongo --version` and `net start MongoDB` (Windows)
- Check connection string in `application.properties`

**JWT Token Expired:**
- Register/login again to get a new token
- Current expiration: 24 hours

**Port 8080 Already in Use:**
- Change `server.port` in `application.properties`

## License

This project is open source and available for educational purposes.
