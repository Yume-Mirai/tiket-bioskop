# Dokumentasi API Sistem Tiket Bioskop

## Daftar Isi
- [Pengenalan](#pengenalan)
- [Fitur Utama](#fitur-utama)
- [Teknologi Stack](#teknologi-stack)
- [Instalasi dan Setup](#instalasi-dan-setup)
- [Konfigurasi](#konfigurasi)
- [Struktur Database](#struktur-database)
- [Autentikasi dan Otorisasi](#autentikasi-dan-otorisasi)
- [API Endpoints](#api-endpoints)
  - [Film Management](#film-management)
  - [Bioskop Management](#bioskop-management)
  - [Jadwal Management](#jadwal-management)
  - [Kursi Management](#kursi-management)
  - [Tiket Management](#tiket-management)
  - [Transaksi Management](#transaksi-management)
  - [User Management](#user-management)
  - [Authentication](#authentication)
  - [Email Management](#email-management)
  - [Forgot Password Management](#forgot-password-management)
  - [Report Management](#report-management)
- [Error Handling](#error-handling)
- [Contoh Penggunaan](#contoh-penggunaan)
- [Testing](#testing)

## Pengenalan

Sistem Tiket Bioskop adalah aplikasi web berbasis REST API yang dikembangkan menggunakan Spring Boot untuk mengelola penjualan tiket bioskop secara online. Aplikasi ini menyediakan platform lengkap untuk pengelolaan film, bioskop, jadwal tayang, kursi, tiket, dan transaksi pembayaran.

## Fitur Utama

### 🎬 **Manajemen Film**
- CRUD operasi untuk film
- Upload dan kompresi poster film
- Pencarian dan filter berdasarkan genre, judul, status
- Advanced search dengan multiple criteria

### 🏢 **Manajemen Bioskop**
- CRUD operasi untuk data bioskop
- Pencarian berdasarkan nama dan lokasi
- Filter berdasarkan lokasi

### 📅 **Manajemen Jadwal**
- CRUD operasi untuk jadwal tayang
- Pengelolaan jadwal per film dan bioskop

### 💺 **Manajemen Kursi**
- CRUD operasi untuk kursi
- Pengelolaan kapasitas dan nomor kursi per bioskop

### 🎫 **Manajemen Tiket**
- CRUD operasi untuk tiket
- Integrasi dengan sistem kursi dan transaksi

### 💳 **Sistem Transaksi**
- Checkout tiket dengan validasi kursi
- Konfirmasi pembayaran dengan kode unik
- Pembatalan transaksi pending
- Tracking status transaksi

### 👥 **Manajemen User**
- Registrasi pengguna baru
- Sistem role (USER, ADMIN)
- Pencarian dan filter user

### 🔐 **Keamanan**
- JWT-based authentication
- Role-based authorization
- Password encryption

### 📧 **Notifikasi Email**
- Email konfirmasi login
- Email tiket setelah pembayaran
- Template HTML untuk email

### 📄 **Laporan PDF**
- Generate tiket PDF
- Generate laporan transaksi
- Export data ke Excel

## Teknologi Stack

### Backend
- **Java 21** - Programming language
- **Spring Boot 3.4.4** - Framework utama
- **Spring Security** - Keamanan aplikasi
- **Spring Data JPA** - ORM dan database operations
- **Hibernate** - ORM implementation

### Database
- **MySQL 8.3.0** - Relational database
- **MySQL Connector/J** - Database driver

### Security & Authentication
- **JWT (JSON Web Token)** - Token-based authentication
- **Spring Security** - Authentication dan authorization

### API Documentation
- **Swagger/OpenAPI 3.0** - API documentation
- **SpringDoc OpenAPI** - Swagger integration

### Email Service
- **Spring Boot Mail** - Email functionality
- **Gmail SMTP** - Email provider

### File Processing
- **Apache POI** - Excel file processing
- **JasperReports** - Advanced PDF generation
- **OpenPDF** - PDF generation

### Utilities
- **Lombok** - Reduce boilerplate code
- **ModelMapper** - Object mapping
- **Hibernate Validator** - Input validation
- **Thymeleaf** - Email templating

## Instalasi dan Setup

### Prerequisites
- Java 21 atau higher
- Maven 3.6+
- MySQL 8.0+
- Git

### Langkah Instalasi

1. **Clone Repository**
   ```bash
   git clone <repository-url>
   cd tiketbioskop
   ```

2. **Setup Database**
   ```sql
   CREATE DATABASE bioskop;
   ```

3. **Konfigurasi Database**
   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/bioskop?createDatabaseIfNotExist=true
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

4. **Setup Email Configuration**
   ```properties
   mail.host=smtp.gmail.com
   mail.port=587
   mail.username=your_email@gmail.com
   mail.password=your_app_password
   ```

5. **Build dan Run Aplikasi**
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

6. **Verify Installation**
   - Aplikasi berjalan di: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - API Docs: http://localhost:8080/api-docs

## Konfigurasi

### Database Configuration
```properties
# Connection
spring.datasource.url=jdbc:mysql://localhost:3306/bioskop
spring.datasource.username=root
spring.datasource.password=password

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

### JWT Configuration
```properties
jwt.secret.key=your-super-secret-key-at-least-32-characters-long
jwt.token.validity=120
jwt.header=Authorization
jwt.prefix=Bearer
```

### Email Configuration
```properties
mail.host=smtp.gmail.com
mail.port=587
mail.username=your_email@gmail.com
mail.password=your_app_password
mail.protocol=smtp
mail.starttls=true
mail.auth=true
```

### File Upload Configuration
```properties
upload.dir=uploads/posters
spring.servlet.multipart.max-file-size=16MB
spring.servlet.multipart.max-request-size=16MB
```

## Struktur Database

### Entity Relationships

```
Users (UserRole) ←→ Transaksi ←→ Tiket ←→ Kursi ←→ Bioskop
     ↑                    ↓           ↓
     └──→ Role       Jadwal ←→ Film
```

### Tabel Utama

#### Users
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Film
```sql
CREATE TABLE film (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    judul VARCHAR(255) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    durasi INT NOT NULL,
    sinopsis TEXT,
    cast VARCHAR(500),
    poster LONGBLOB,
    trailer_url VARCHAR(255),
    status ENUM('SEDANG_TAYANG', 'AKAN_DATANG', 'SUDAH_TAYANG') DEFAULT 'AKAN_DATANG'
);
```

#### Bioskop
```sql
CREATE TABLE bioskop (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nama VARCHAR(255) NOT NULL,
    lokasi VARCHAR(255) NOT NULL,
    kapasitas INT NOT NULL
);
```

#### Kursi
```sql
CREATE TABLE kursi (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nomor VARCHAR(10) NOT NULL,
    bioskop_id BIGINT NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (bioskop_id) REFERENCES bioskop(id)
);
```

#### Jadwal
```sql
CREATE TABLE jadwal (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    film_id BIGINT NOT NULL,
    bioskop_id BIGINT NOT NULL,
    waktu_tayang DATETIME NOT NULL,
    harga DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (film_id) REFERENCES film(id),
    FOREIGN KEY (bioskop_id) REFERENCES bioskop(id)
);
```

#### Transaksi
```sql
CREATE TABLE transaksi (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    jadwal_id BIGINT NOT NULL,
    kode_pembayaran VARCHAR(50) UNIQUE NOT NULL,
    total_harga DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING', 'PAID', 'CANCELLED', 'EXPIRED') DEFAULT 'PENDING',
    expired_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (jadwal_id) REFERENCES jadwal(id)
);
```

## Autentikasi dan Otorisasi

### Role Hierarchy
- **USER**: Akses ke fitur pembelian tiket dan melihat transaksi sendiri
- **ADMIN**: Akses penuh ke semua fitur termasuk manajemen data

### JWT Token Structure
```json
{
  "sub": "username",
  "roles": ["USER", "ADMIN"],
  "iat": 1609459200,
  "exp": 1609460400
}
```

### Authentication Flow
1. User login dengan username/password
2. Server generate JWT token
3. Token dikirim ke email user
4. User menggunakan token untuk API calls
5. Server validate token pada setiap request

## API Endpoints

### Base URL
```
http://localhost:8080
```

### Authentication Header
```
Authorization: Bearer <jwt_token>
```

---

## 📋 **DAFTAR LENGKAP SEMUA ENDPOINTS**

---

## 🎬 **Film Management (10 Endpoints)**

### 1. Get All Films (Public)
```http
GET /all/film
```

**Parameters:**
- `page` (optional): Default 0
- `size` (optional): Default 10
- `sortBy` (optional): Default "id"
- `sortDir` (optional): Default "asc"

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "judul": "Avengers: Endgame",
      "genre": "Action",
      "durasi": 181,
      "sinopsis": "Description...",
      "cast": "Robert Downey Jr., Chris Evans",
      "trailerUrl": "https://youtube.com/watch?v=...",
      "status": "SEDANG_TAYANG"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

### 2. Get Film by ID (Public)
```http
GET /all/film/{id}
```

**Response:**
```json
{
  "id": 1,
  "judul": "Avengers: Endgame",
  "genre": "Action",
  "durasi": 181,
  "sinopsis": "Description...",
  "cast": "Robert Downey Jr., Chris Evans",
  "trailerUrl": "https://youtube.com/watch?v=...",
  "status": "SEDANG_TAYANG"
}
```

### 3. Get Film Poster (Public)
```http
GET /all/film/{id}/poster
```

**Response:** Binary image data (JPEG/PNG)

### 4. Create Film (Admin Only)
```http
POST /admin/film
Content-Type: multipart/form-data
```

**Parameters:**
- `judul` (required): String
- `genre` (required): String
- `durasi` (required): Integer
- `sinopsis` (required): String
- `cast` (required): String
- `poster` (required): MultipartFile
- `trailerUrl` (optional): String
- `status` (optional): Enum (SEDANG_TAYANG, AKAN_DATANG, SUDAH_TAYANG)

**Response:**
```json
{
  "success": true,
  "message": "Film berhasil dibuat",
  "data": {
    "id": 1,
    "judul": "Avengers: Endgame",
    "genre": "Action",
    "durasi": 181,
    "sinopsis": "Description...",
    "cast": "Robert Downey Jr., Chris Evans",
    "trailerUrl": "https://youtube.com/watch?v=...",
    "status": "SEDANG_TAYANG"
  },
  "timestamp": "2024-01-01T10:00:00"
}
```

### 5. Update Film (Admin Only)
```http
PUT /admin/film/{id}
Content-Type: multipart/form-data
```

**Parameters:** Sama seperti create, kecuali `poster` bersifat optional

### 6. Delete Film (Admin Only)
```http
DELETE /admin/film/{id}
```

**Response:** HTTP 204 No Content

### 7. Get Films by Genre (Public)
```http
GET /all/film/genre?genre=Action&page=0&size=10&sortBy=id&sortDir=asc
```

### 8. Search Films by Title (Public)
```http
GET /all/film/search?title=Avengers&page=0&size=10&sortBy=id&sortDir=asc
```

### 9. Filter Films by Status (Public)
```http
GET /all/film/filter?status=SEDANG_TAYANG&page=0&size=10&sortBy=judul&sortDir=asc
```

### 10. Advanced Search Films (Public)
```http
GET /all/film/advanced-search?judul=Avengers&genre=Action&status=SEDANG_TAYANG&minDurasi=120&maxDurasi=200&page=0&size=10&sortBy=judul&sortDir=asc
```

---

## 🏢 **Bioskop Management (7 Endpoints)**

### 1. Get All Bioskop (User/Admin)
```http
GET /all/bioskop?page=0&size=10&sortBy=id&sortDir=asc
```

**Response:**
```json
{
  "code": 200,
  "message": "Berhasil ambil data",
  "data": {
    "content": [
      {
        "id": 1,
        "nama": "CGV Grand Indonesia",
        "lokasi": "Jakarta Pusat",
        "kapasitas": 200
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0
  }
}
```

### 2. Get Bioskop by ID (User/Admin)
```http
GET /all/bioskop/{id}
```

### 3. Search Bioskop by Name (User/Admin)
```http
GET /all/bioskop/search?nama=CGV&page=0&size=10&sortBy=nama&sortDir=asc
```

### 4. Filter Bioskop by Location (User/Admin)
```http
GET /all/bioskop/filter?lokasi=Jakarta&page=0&size=10&sortBy=nama&sortDir=asc
```

### 5. Create Bioskop (Admin Only)
```http
POST /admin/bioskop
Content-Type: application/json
```

**Request Body:**
```json
{
  "nama": "CGV Grand Indonesia",
  "lokasi": "Jakarta Pusat",
  "kapasitas": 200
}
```

### 6. Update Bioskop (Admin Only)
```http
PUT /admin/bioskop/{id}
Content-Type: application/json
```

### 7. Delete Bioskop (Admin Only)
```http
DELETE /admin/bioskop/{id}
```

---

## 📅 **Jadwal Management (5 Endpoints)**

### 1. Get All Jadwal (User/Admin)
```http
GET /all/jadwal?page=0&size=10&sortBy=id&sortDir=asc
```

### 2. Get Jadwal by ID (User/Admin)
```http
GET /all/jadwal/{id}
```

### 3. Create Jadwal (Admin Only)
```http
POST /admin/jadwal
Content-Type: application/json
```

**Request Body:**
```json
{
  "filmId": 1,
  "bioskopId": 1,
  "waktuTayang": "2024-01-01T14:00:00",
  "harga": 50000.00
}
```

### 4. Update Jadwal (Admin Only)
```http
PUT /admin/jadwal/{id}
Content-Type: application/json
```

### 5. Delete Jadwal (Admin Only)
```http
DELETE /admin/jadwal/{id}
```

---

## Kursi Management

### 1. Get All Kursi (User/Admin)
```http
GET /all/kursi?page=0&size=10&sortBy=id&sortDir=asc
```

### 2. Get Kursi by ID (User/Admin)
```http
GET /all/kursi/{id}
```

### 3. Create Kursi (Admin Only)
```http
POST /admin/kursi
Content-Type: application/json
```

**Request Body:**
```json
{
  "nomor": "A1",
  "bioskopId": 1,
  "isAvailable": true
}
```

### 4. Update Kursi (Admin Only)
```http
PUT /admin/kursi/{id}
Content-Type: application/json
```

### 5. Delete Kursi (Admin Only)
```http
DELETE /admin/kursi/{id}
```

---

## Tiket Management

### 1. Get All Tiket (Admin Only)
```http
GET /admin/tiket?page=0&size=10&sortBy=id&sortDir=asc
```

### 2. Get Tiket by ID (Admin Only)
```http
GET /admin/tiket/{id}
```

### 3. Create Tiket (Admin Only)
```http
POST /admin/tiket
Content-Type: application/json
```

**Request Body:**
```json
{
  "transaksiId": 1,
  "kursiId": 1
}
```

### 4. Update Tiket (Admin Only)
```http
PUT /admin/tiket/{id}
Content-Type: application/json
```

### 5. Delete Tiket (Admin Only)
```http
DELETE /admin/tiket/{id}
```

---

## 💳 **Transaksi Management (6 Endpoints)**

### 1. Checkout Tiket (User)
```http
POST /api/transaksi/checkout
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Request Body:**
```json
{
  "jadwalId": 1,
  "kursiIds": [1, 2, 3],
  "jumlahTiket": 3
}
```

**Response:**
```json
{
  "success": true,
  "message": "Checkout berhasil, silakan lakukan pembayaran dalam 5 menit",
  "data": {
    "transaksiId": 1,
    "kodePembayaran": "TRX-20240101-001",
    "totalHarga": 150000.00,
    "expiredAt": "2024-01-01T10:05:00",
    "status": "PENDING"
  },
  "timestamp": "2024-01-01T10:00:00"
}
```

### 2. Konfirmasi Pembayaran (User)
```http
POST /api/transaksi/konfirmasi
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Request Body:**
```json
{
  "kodePembayaran": "TRX-20240101-001"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Pembayaran berhasil dikonfirmasi. Tiket akan dikirim ke email Anda.",
  "data": "Transaksi berhasil",
  "timestamp": "2024-01-01T10:15:00"
}
```

### 3. Batalkan Transaksi (User)
```http
POST /api/transaksi/cancel/{transaksiId}
Authorization: Bearer <jwt_token>
```

### 4. Get My Transactions (User)
```http
GET /api/transaksi/my-transactions?page=0&size=10&sortBy=id&sortDir=desc
Authorization: Bearer <jwt_token>
```

### 5. Filter My Transactions by Status (User)
```http
GET /api/transaksi/filter?status=PAID&page=0&size=10&sortBy=createdAt&sortDir=desc
Authorization: Bearer <jwt_token>
```

### 6. Search My Transactions (User)
```http
GET /api/transaksi/search?kodePembayaran=TRX-20240101-001&page=0&size=10&sortBy=createdAt&sortDir=desc
Authorization: Bearer <jwt_token>
```

---

## 👥 **User Management (4 Endpoints)**

### 1. Register User (Public)
```http
POST /users/register
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Berhasil menambahkan user baru",
  "data": null
}
```

### 2. Get All Users (Admin Only)
```http
GET /users/all?page=0&size=10&sortBy=id&sortDir=asc
Authorization: Bearer <jwt_token>
```

### 3. Search Users (Admin Only)
```http
GET /users/search?keyword=john&page=0&size=10&sortBy=username&sortDir=asc
Authorization: Bearer <jwt_token>
```

### 4. Filter Users by Status (Admin Only)
```http
GET /users/filter?status=true&page=0&size=10&sortBy=username&sortDir=asc
Authorization: Bearer <jwt_token>
```

---

## 🔐 **Authentication (1 Endpoint)**

### 1. Login
```http
POST /login
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "johndoe",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login berhasil! Token akses telah dikirim ke email Anda.",
  "data": {
    "username": "johndoe",
    "email": "john@example.com",
    "roles": ["USER"]
  }
}
```

---

## 📧 **Email Management (1 Endpoint) - Admin Only**

### 1. Send Email (Admin Only)
```http
POST /admin/email/send
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Request Body:**
```json
{
  "to": "user@example.com",
  "subject": "Test Email",
  "body": "This is a test email content"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Successfully send Email",
  "data": null
}
```

---

## 🔑 **Forgot Password Management (3 Endpoints) - Public**

### 1. Request OTP
```http
POST /user/forgot-password/request?email=user@example.com
```

**Response:** `"OTP telah dikirim ke email."`

### 2. Verify OTP
```http
POST /user/forgot-password/verify?email=user@example.com&otp=123456
```

**Response:** `"OTP valid. Anda dapat mereset password."`

### 3. Reset Password
```http
POST /user/forgot-password/reset?email=user@example.com&newPassword=newpassword123
```

**Response:** `"Password berhasil diubah."`

---

## 📊 **Report Management (4 Endpoints) - Admin Only**

### 1. Generate Excel Report (Users)
```http
GET /admin/report/generate-excel
Authorization: Bearer <jwt_token>
```

**Response:** File Excel dengan data user dan admin

### 2. Generate PDF Report (All Transactions)
```http
GET /api/laporan/pdf/transaksi
Authorization: Bearer <jwt_token>
```

**Response:** File PDF dengan laporan semua transaksi

### 3. Generate Ticket PDF (Specific Transaction)
```http
GET /api/laporan/tiket/pdf/{transaksiId}
Authorization: Bearer <jwt_token>
```

**Response:** File PDF tiket untuk transaksi tertentu

### 4. Download Ticket PDF (Stream)
```http
GET /api/laporan/tiket/download/{transaksiId}
Authorization: Bearer <jwt_token>
```

**Response:** Stream download tiket PDF

---

## Error Handling

| Code | Description |
|------|-------------|
| 200 | Success |
| 201 | Created |
| 204 | No Content |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 409 | Conflict |
| 500 | Internal Server Error |

### Error Response Format
```json
{
  "success": false,
  "message": "Error description",
  "timestamp": "2024-01-01T10:00:00"
}
```

### Common Error Messages

#### Authentication Errors
- `"Invalid username or password"` - Username/password salah
- `"Token has expired"` - JWT token sudah kadaluarsa
- `"Access denied"` - Tidak memiliki permission

#### Validation Errors
- `"Field 'judul' is required"` - Field wajib diisi
- `"Invalid email format"` - Format email tidak valid
- `"Kursi sudah tidak tersedia"` - Kursi sudah dipesan

#### Business Logic Errors
- `"Transaksi sudah kadaluarsa"` - Transaksi melebihi batas waktu
- `"Insufficient balance"` - Saldo tidak mencukupi
- `"Film tidak sedang tayang"` - Film tidak dalam status tayang

## Contoh Penggunaan

### Menggunakan cURL

#### 1. Login
```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "password123"
  }'
```

#### 2. Get All Films
```bash
curl -X GET "http://localhost:8080/all/film?page=0&size=5" \
  -H "Content-Type: application/json"
```

#### 3. Create Film (Admin)
```bash
curl -X POST http://localhost:8080/admin/film \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "judul=Spider-Man: No Way Home" \
  -F "genre=Action" \
  -F "durasi=148" \
  -F "sinopsis=Peter Parker seeks help from Doctor Strange" \
  -F "cast=Tom Holland, Zendaya" \
  -F "poster=@spiderman.jpg" \
  -F "status=SEDANG_TAYANG"
```

### Menggunakan JavaScript (Fetch API)

#### Login
```javascript
const login = async (username, password) => {
  const response = await fetch('http://localhost:8080/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      username,
      password
    })
  });

  const data = await response.json();
  if (data.success) {
    // Token dikirim ke email
    console.log('Login berhasil, silakan cek email');
  }
  return data;
};
```

#### Get Films
```javascript
const getFilms = async (page = 0, size = 10) => {
  const response = await fetch(
    `http://localhost:8080/all/film?page=${page}&size=${size}`
  );
  return await response.json();
};
```

#### Checkout
```javascript
const checkout = async (jadwalId, kursiIds, token) => {
  const response = await fetch('http://localhost:8080/api/transaksi/checkout', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
      jadwalId,
      kursiIds,
      jumlahTiket: kursiIds.length
    })
  });

  return await response.json();
};
```

### Menggunakan Python (requests)

#### Login
```python
import requests

def login(username, password):
    response = requests.post('http://localhost:8080/login',
                           json={
                               'username': username,
                               'password': password
                           })
    return response.json()

# Usage
result = login('johndoe', 'password123')
print(result)
```

#### Get Films
```python
import requests

def get_films(page=0, size=10):
    response = requests.get(f'http://localhost:8080/all/film',
                          params={
                              'page': page,
                              'size': size
                          })
    return response.json()

# Usage
films = get_films()
print(films)
```

#### Create Film (Admin)
```python
import requests

def create_film(film_data, token):
    headers = {
        'Authorization': f'Bearer {token}'
    }

    # Untuk multipart form data
    response = requests.post('http://localhost:8080/admin/film',
                           files=film_data,
                           headers=headers)
    return response.json()

# Usage
film_data = {
    'judul': 'Spider-Man: No Way Home',
    'genre': 'Action',
    'durasi': 148,
    'sinopsis': 'Peter Parker seeks help from Doctor Strange',
    'cast': 'Tom Holland, Zendaya',
    'poster': open('spiderman.jpg', 'rb'),
    'status': 'SEDANG_TAYANG'
}

result = create_film(film_data, 'YOUR_JWT_TOKEN')
print(result)
```

## Testing

### Manual Testing dengan Swagger UI
1. Buka http://localhost:8080/swagger-ui.html
2. Klik endpoint yang ingin ditest
3. Masukkan parameter yang diperlukan
4. Klik "Try it out"
5. Lihat response

### Automated Testing
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=YourTestClass
```

### Database Testing
1. Setup test database
2. Run aplikasi dengan profile test
3. Execute integration tests

## 📋 **Data Transfer Objects (DTOs)**

### Film DTOs
```java
// Request DTO untuk membuat film baru
public class FilmRequestDTO {
    private String judul;
    private String genre;
    private int durasi;
    private String sinopsis;
    private String cast;
    private String trailerUrl;
    private StatusFilm status;
}

// Response DTO untuk data film
public class FilmResponseDTO {
    private Long id;
    private String judul;
    private String genre;
    private int durasi;
    private String sinopsis;
    private String cast;
    private String trailerUrl;
    private StatusFilm status;
}
```

### Transaction DTOs
```java
// Request untuk checkout
public class CheckoutRequestDTO {
    private Long jadwalId;
    private List<Long> kursiIds;
    private int jumlahTiket;
}

// Response setelah checkout
public class CheckoutResponseDTO {
    private Long transaksiId;
    private String kodePembayaran;
    private BigDecimal totalHarga;
    private LocalDateTime expiredAt;
    private String status;
}
```

### User DTOs
```java
// Request untuk registrasi
public class RegisterUserDto {
    private String username;
    private String email;
    private String password;
}

// Response untuk detail user
public class UserDetailDto {
    private Long id;
    private String username;
    private String email;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
```

## 🔧 **Troubleshooting**

### Common Issues

#### Database Connection Error
```bash
# Pastikan MySQL service berjalan
sudo systemctl start mysql

# Cek konfigurasi di application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/bioskop
spring.datasource.username=root
spring.datasource.password=your_password
```

#### JWT Token Issues
```bash
# Pastikan JWT secret key di application.properties
jwt.secret.key=your-super-secret-key-at-least-32-characters-long

# Token validity dalam menit
jwt.token.validity=120
```

#### Email Configuration Issues
```bash
# Setup Gmail App Password
# 1. Enable 2FA di Gmail
# 2. Generate App Password
# 3. Set di application.properties
mail.password=your-16-character-app-password
```

#### File Upload Issues
```bash
# Pastikan konfigurasi upload di application.properties
spring.servlet.multipart.max-file-size=16MB
spring.servlet.multipart.max-request-size=16MB

# Direktori upload harus dapat diakses
upload.dir=uploads/posters
```

## 📈 **Performance Considerations**

### Database Optimization
- **Indexing**: Pastikan field yang sering di-query memiliki index
- **Pagination**: Selalu gunakan pagination untuk query besar
- **Connection Pooling**: Konfigurasi connection pool untuk production

### API Optimization
- **Caching**: Implementasi cache untuk data static (film, bioskop)
- **Compression**: Enable gzip compression untuk response
- **Rate Limiting**: Implementasi rate limiting untuk mencegah abuse

### Security Best Practices
- **HTTPS**: Selalu gunakan HTTPS di production
- **Input Validation**: Validasi semua input dari user
- **SQL Injection Prevention**: Gunakan parameterized queries
- **CORS**: Konfigurasi CORS policy dengan benar

## 🚀 **Deployment**

### Production Checklist
- [ ] Database production siap
- [ ] Environment variables configured
- [ ] HTTPS/SSL certificate installed
- [ ] Monitoring dan logging setup
- [ ] Backup strategy implemented
- [ ] Security headers configured

### Docker Deployment
```dockerfile
FROM openjdk:21-jre-slim
COPY target/tiketbioskop-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Environment Variables
```bash
# Database
DB_URL=jdbc:mysql://db-host:3306/bioskop
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password

# JWT
JWT_SECRET=your-production-secret-key

# Email
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
```

## 📞 **Support**

Untuk pertanyaan atau masalah teknis, silakan hubungi tim development atau buat issue di repository project.

---

## 📊 **API Endpoints Summary**

| Controller | Endpoints | Public/Admin |
|------------|-----------|--------------|
| Film | 10 | Mixed |
| Bioskop | 7 | Mixed |
| Jadwal | 5 | Mixed |
| Kursi | 5 | Mixed |
| Tiket | 5 | Admin Only |
| Transaksi | 6 | User (own) / Admin (all) |
| User | 4 | Mixed |
| Authentication | 1 | Public |
| Email | 1 | Admin Only |
| Forgot Password | 3 | Public |
| Report | 4 | Admin Only |
| **TOTAL** | **51 Endpoints** | **Complete** |

---

**Dibuat dengan ❤️ menggunakan Spring Boot dan dokumentasi lengkap untuk developer**

**🎯 Status: 100% Complete - All Controllers Documented**