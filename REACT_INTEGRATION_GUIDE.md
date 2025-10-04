# Panduan Integrasi Spring Boot dengan React Vite

## Overview
Dokumen ini berisi panduan lengkap untuk mengintegrasikan aplikasi Spring Boot sistem tiket bioskop dengan React menggunakan Vite sebagai build tool.

## Perubahan yang Telah Dibuat

### 1. Update Dependencies (pom.xml)
**File yang diubah:** `pom.xml`

**Perubahan:**
- Menambahkan WebSocket starter untuk real-time updates
- Menambahkan Jackson dependencies untuk JSON processing yang lebih baik
- Update versi Spring Boot ke 3.4.4

### 2. Konfigurasi CORS
**File yang diubah:** `src/main/java/com/uasjava/tiketbioskop/config/CorsConfig.java`

**Perubahan:**
- Menambahkan support untuk Vite development server (port 5173)
- Menambahkan support untuk React development server (port 3000)
- Update allowed origins untuk production domain

### 3. Konfigurasi Aplikasi
**File yang diubah:** `src/main/resources/application.properties`

**Perubahan:**
- Menambahkan konfigurasi static file serving
- Update Jackson configuration untuk JSON response
- Menambahkan konfigurasi upload dengan validasi
- Menambahkan logging configuration untuk debugging

**File baru:** `src/main/resources/application-dev.properties`
**File baru:** `src/main/resources/application-prod.properties`

### 4. Web Configuration
**File baru:** `src/main/java/com/uasjava/tiketbioskop/config/WebConfig.java`

**Fitur:**
- Static file serving untuk uploads
- Support untuk React production build
- Resource handler untuk berbagai path

### 5. API Response Structure
**File baru:** `src/main/java/com/uasjava/tiketbioskop/dto/ApiResponse.java`

**Fitur:**
- Standardized response wrapper untuk semua API
- Support untuk single data, list data, dan paginated data
- Built-in error handling dan success responses
- Metadata support untuk informasi tambahan

**File baru:** `src/main/java/com/uasjava/tiketbioskop/dto/PaginationMetadata.java`

**Fitur:**
- Metadata untuk pagination yang frontend-friendly
- Informasi lengkap tentang halaman, ukuran, total, dll.

### 6. Error Handling
**File yang diubah:** `src/main/java/com/uasjava/tiketbioskop/exception/GlobalExceptionHandler.java`

**Perubahan:**
- Update semua exception handler untuk menggunakan ApiResponse
- Menambahkan error codes yang spesifik
- Menambahkan handler untuk file upload errors
- Menambahkan handler untuk constraint violations
- Menambahkan path information untuk debugging

### 7. File Upload Utility
**File baru:** `src/main/java/com/uasjava/tiketbioskop/util/FileUploadUtil.java`

**Fitur:**
- Validasi file yang komprehensif (ukuran, tipe, ekstensi)
- Secure file naming dengan UUID
- Support untuk multiple subfolders
- File deletion dan existence checking

### 8. Security Configuration
**File yang diubah:** `src/main/java/com/uasjava/tiketbioskop/config/SecurityConfig.java`

**Perubahan:**
- Menambahkan security headers untuk SPA
- Update endpoint permissions untuk React integration
- Menambahkan static file serving permissions
- HSTS configuration untuk production

## Cara Setup Development Environment

### 1. Backend Setup

```bash
# 1. Pastikan Maven terinstall
mvn --version

# 2. Jalankan aplikasi dalam mode development
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 3. Aplikasi akan berjalan di http://localhost:8080
```

### 2. React Vite Setup

```bash
# 1. Buat project React Vite baru
npm create vite@latest tiketbioskop-frontend -- --template react
cd tiketbioskop-frontend

# 2. Install dependencies
npm install

# 3. Install axios untuk HTTP client
npm install axios

# 4. Jalankan development server
npm run dev
# Aplikasi akan berjalan di http://localhost:5173
```

### 3. Konfigurasi API Client

Buat file `src/services/api.js`:

```javascript
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor untuk menambahkan JWT token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor untuk handling error
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

### 4. Environment Variables untuk React

Buat file `.env` di root project React:

```env
VITE_API_URL=http://localhost:8080
VITE_APP_NAME=TiketBioskop
```

### 5. Contoh Penggunaan API di React

```javascript
// services/filmService.js
import api from './api';

export const filmService = {
  getAllFilms: (params) => api.get('/all/film', { params }),
  getFilmById: (id) => api.get(`/all/film/${id}`),
  getFilmPoster: (id) => api.get(`/all/film/${id}/poster`, {
    responseType: 'blob'
  }),
  createFilm: (formData) => api.post('/admin/film', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  updateFilm: (id, formData) => api.put(`/admin/film/${id}`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  deleteFilm: (id) => api.delete(`/admin/film/${id}`),
};
```

## Production Deployment

### 1. Build React untuk Production

```bash
# Build React app
npm run build

# Copy build files ke Spring Boot static resources
# atau serve dari web server terpisah (nginx, apache)
```

### 2. Environment Variables untuk Production

Buat file `.env.production`:

```bash
# Backend Environment Variables
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
JWT_SECRET_KEY=your_super_secret_jwt_key_here
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
FRONTEND_URL=https://yourdomain.com

# React Environment Variables
VITE_API_URL=https://yourdomain.com
```

### 3. Menjalankan Production Build

```bash
# Dengan profile production
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# atau dengan environment variables
java -jar target/tiketbioskop-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --DB_USERNAME=your_username \
  --DB_PASSWORD=your_password
```

## Struktur Project yang Disarankan

```
tiketbioskop/
├── backend/                 # Spring Boot project (current)
│   ├── src/main/resources/
│   │   ├── static/         # React production build
│   │   └── uploads/        # File uploads
│   └── pom.xml
└── frontend/               # React Vite project
    ├── src/
    │   ├── components/
    │   ├── services/
    │   ├── pages/
    │   └── utils/
    ├── public/
    └── package.json
```

## API Endpoints yang Tersedia

### Public Endpoints
- `POST /login` - User login
- `POST /users/register` - User registration
- `POST /email/send` - Send email

### User Endpoints (memerlukan autentikasi)
- `GET /all/film` - Get semua film dengan pagination
- `GET /all/film/{id}` - Get film by ID
- `GET /all/film/{id}/poster` - Get film poster
- `GET /all/bioskop` - Get semua bioskop
- `GET /all/jadwal` - Get semua jadwal
- `GET /all/kursi/available/{jadwalId}` - Get kursi yang tersedia
- `POST /api/transaksi/checkout` - Checkout transaksi
- `GET /api/transaksi/my-transactions` - Get transaksi user sendiri

### Admin Endpoints (memerlukan role ADMIN)
- `POST /admin/film` - Create film baru
- `PUT /admin/film/{id}` - Update film
- `DELETE /admin/film/{id}` - Delete film
- `GET /admin/**` - Admin management endpoints

## Response Format

### Success Response
```json
{
  "success": true,
  "message": "Data berhasil diambil",
  "data": { ... },
  "timestamp": "2024-01-01T10:30:00"
}
```

### Paginated Response
```json
{
  "success": true,
  "message": "Data berhasil diambil dengan pagination",
  "dataList": [...],
  "metadata": {
    "currentPage": 0,
    "pageSize": 10,
    "totalElements": 100,
    "totalPages": 10,
    "first": true,
    "last": false,
    "hasNext": true,
    "hasPrevious": false,
    "sortBy": "id",
    "sortDirection": "asc"
  },
  "timestamp": "2024-01-01T10:30:00"
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "errorCode": "SPECIFIC_ERROR_CODE",
  "timestamp": "2024-01-01T10:30:00",
  "path": "/api/endpoint"
}
```

## Tips untuk Development

1. **CORS Issues**: Pastikan Vite dev server port sudah ditambahkan ke CORS configuration
2. **File Upload**: Gunakan FormData untuk upload file dengan poster
3. **Error Handling**: Selalu handle error response dari API
4. **Loading States**: Implement loading states untuk better UX
5. **Token Management**: Handle JWT token expiration dan refresh

## Troubleshooting

### Common Issues

1. **CORS Error**
   - Pastikan port Vite (5173) sudah diizinkan di CORS config
   - Check browser network tab untuk error details

2. **File Upload Error**
   - Pastikan ukuran file tidak melebihi batas (10MB)
   - Pastikan format file didukung (JPG, PNG, GIF, WebP)

3. **Authentication Error**
   - Pastikan JWT token valid dan tidak expired
   - Check token di localStorage atau sessionStorage

4. **Build Error**
   - Pastikan semua dependencies sudah terinstall
   - Check untuk konflik versi package

## Kesimpulan

Dengan perubahan yang telah dibuat, aplikasi Spring Boot Anda sudah siap untuk diintegrasikan dengan React Vite. Ikuti panduan setup di atas untuk memulai development dan pastikan untuk menggunakan environment-specific configurations untuk production deployment.