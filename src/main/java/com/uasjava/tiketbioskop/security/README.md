# Sistem Otorisasi dan Keamanan - Tiket Bioskop

## Ringkasan Perbaikan

Sistem otorisasi telah diperbaiki secara menyeluruh untuk memastikan keamanan endpoint dan validasi role yang ketat.

## Perubahan yang Dilakukan

### 1. Perbaikan JwtProvider
- ✅ Menggunakan `Keys.hmacShaKeyFor()` untuk signature yang lebih aman
- ✅ Menggunakan `JwtParserBuilder` untuk parser yang lebih modern
- ✅ Menambahkan validasi token yang lebih ketat

### 2. Perbaikan JwtFilter
- ✅ Error handling yang komprehensif untuk berbagai jenis exception JWT
- ✅ Validasi claims yang lebih ketat sebelum membuat authentication
- ✅ Response JSON yang informatif untuk error autentikasi
- ✅ Logging yang lebih baik untuk debugging

### 3. Perbaikan SecurityConfig
- ✅ Konfigurasi endpoint yang lebih spesifik dan aman
- ✅ Pemisahan yang jelas antara endpoint public, user-only, dan admin-only
- ✅ Menghilangkan konfigurasi yang terlalu permisif

### 4. Utilitas Otorisasi Baru
- ✅ `AuthorizationUtil` untuk validasi role dan permission
- ✅ Method helper untuk pengecekan kepemilikan resource
- ✅ Validasi role yang mudah digunakan di seluruh aplikasi

### 5. Exception Handling
- ✅ `AuthorizationException` untuk error otorisasi custom
- ✅ Handler di `GlobalExceptionHandler` untuk semua jenis error autentikasi
- ✅ Response yang konsisten untuk error keamanan

### 6. Pengujian Komprehensif
- ✅ Test class untuk semua skenario otorisasi
- ✅ Test untuk role-based access control
- ✅ Test untuk endpoint protection

## Struktur Endpoint Lengkap

### Public Endpoints (tidak perlu autentikasi)
- `POST /users/register` - Registrasi user baru
- `POST /login` - Login user (mengembalikan accessToken)
- `POST /email/send` - Kirim email
- `GET /api-docs/**` - Dokumentasi API
- `GET /swagger-ui/**` - Swagger UI

### User Endpoints (ROLE_USER atau ROLE_ADMIN)
- `GET /all/film/**` - Melihat daftar film (read-only)
- `GET /all/bioskop/**` - Melihat daftar bioskop (read-only)
- `GET /all/jadwal/**` - Melihat daftar jadwal (read-only)
- `GET /all/kursi/**` - Melihat daftar kursi (read-only)
- `GET /api/transaksi/checkout` - Checkout tiket
- `POST /api/transaksi/konfirmasi` - Konfirmasi pembayaran
- `POST /api/transaksi/cancel/{id}` - Batalkan transaksi
- `GET /api/transaksi/my-transactions` - Lihat transaksi sendiri
- `GET /api/transaksi/filter` - Filter transaksi sendiri
- `GET /api/transaksi/search` - Cari transaksi sendiri
- `GET /api/laporan/tiket/pdf/{id}` - Download tiket sendiri

### Admin Endpoints (hanya ROLE_ADMIN)
- `GET /users/all` - Lihat semua user
- `GET /users/search` - Cari user
- `GET /users/filter` - Filter user
- `POST /admin/bioskop` - Buat bioskop baru
- `PUT /admin/bioskop/{id}` - Update bioskop
- `DELETE /admin/bioskop/{id}` - Hapus bioskop
- `POST /admin/jadwal` - Buat jadwal baru
- `PUT /admin/jadwal/{id}` - Update jadwal
- `DELETE /admin/jadwal/{id}` - Hapus jadwal
- `POST /admin/kursi` - Buat kursi baru
- `PUT /admin/kursi/{id}` - Update kursi
- `DELETE /admin/kursi/{id}` - Hapus kursi
- `POST /admin/tiket` - Buat tiket baru
- `PUT /admin/tiket/{id}` - Update tiket
- `DELETE /admin/tiket/{id}` - Hapus tiket
- `GET /all/tiket/**` - Lihat semua tiket (admin only)
- `GET /api/laporan/**` - Generate laporan

## CRUD Operations yang Telah Diperbaiki

### 1. Bioskop Management
- ✅ **Entity**: JPA annotations lengkap dengan @OneToMany ke Jadwal
- ✅ **Repository**: Query methods untuk pencarian dan validasi unik
- ✅ **Service**: Business logic dengan validasi nama unik dan constraint checking
- ✅ **Controller**: REST endpoints dengan @PreAuthorize security
- ✅ **Validasi**: Nama bioskop harus unik, tidak boleh hapus jika ada jadwal aktif

### 2. Jadwal Management
- ✅ **Entity**: Relationships dengan Film dan Bioskop
- ✅ **Repository**: Query methods untuk cek konflik jadwal dan availability
- ✅ **Service**: Validasi konflik waktu, tanggal di masa depan, jam tayang valid
- ✅ **Controller**: REST endpoints dengan security annotations
- ✅ **Validasi**: Tidak boleh konflik waktu di bioskop yang sama

### 3. Kursi Management
- ✅ **Entity**: Enum untuk tipe kursi (REGULER, VIP, VVIP)
- ✅ **Repository**: Query untuk cek kursi tersedia dan validasi unik per bioskop
- ✅ **Service**: Validasi nomor kursi unik per bioskop, auto-uppercase
- ✅ **Controller**: REST endpoints dengan proper security
- ✅ **Validasi**: Nomor kursi unik dalam satu bioskop

### 4. Tiket Management
- ✅ **Entity**: Relationships dengan Transaksi dan Kursi
- ✅ **Repository**: Query untuk cek tiket berdasarkan transaksi
- ✅ **Service**: Validasi kursi tidak double-booking, harga valid
- ✅ **Controller**: REST endpoints dengan admin-only access
- ✅ **Validasi**: Kursi dan transaksi harus kompatibel

## Default Parameter Values untuk Pagination

Semua endpoint GET dengan pagination telah dikonfigurasi dengan default values sebagai berikut:

### Parameter Defaults:
- **page**: `0` (halaman pertama)
- **size**: `10000` (jumlah data yang besar untuk mengambil semua record)
- **sortBy**: `id` (primary key untuk sorting yang konsisten)
- **sortDir**: `asc` (ascending order untuk urutan yang predictable)

### Contoh Penggunaan:

**Tanpa Parameter (menggunakan defaults):**
```
GET /all/bioskop
```
→ Mengambil semua bioskop dari halaman 0 dengan ukuran 10000, sort by id ascending

**Dengan Parameter Custom:**
```
GET /all/bioskop?page=0&size=50&sortBy=nama&sortDir=desc
```
→ Mengambil 50 bioskop pertama, diurutkan berdasarkan nama descending

**Mengambil Semua Data:**
```
GET /all/bioskop?page=0&size=10000
```
→ Mengambil semua bioskop yang ada dalam satu request

### Keuntungan Konfigurasi Ini:
- ✅ **Efisien**: Tidak perlu multiple requests untuk mendapatkan semua data
- ✅ **Konsisten**: Default values yang sama di semua controller
- ✅ **Fleksibel**: Tetap bisa menggunakan custom pagination jika diperlukan
- ✅ **User-Friendly**: API consumer bisa mendapatkan semua data dengan sekali request

## PDF Generation yang Diperbaiki

### Masalah yang Ditemukan:
- ❌ **Sebelumnya**: Service generate konten text biasa tapi mengklaim sebagai PDF
- ❌ **Content-Type**: Salah di-set sebagai `application/pdf`
- ❌ **File Extension**: Menggunakan `.pdf` tapi isi adalah text biasa
- ❌ **PDF Reader**: Tidak bisa membaca file karena bukan format PDF sebenarnya

### Solusi yang Diterapkan:
- ✅ **OpenPDF Library**: Menggunakan `com.github.librepdf:openpdf` untuk generate PDF asli
- ✅ **Proper PDF Format**: Membuat dokumen PDF dengan tabel, header, dan formatting yang benar
- ✅ **Content-Type**: Tetap `application/pdf` karena sekarang benar-benar PDF
- ✅ **Readable PDFs**: File dapat dibuka dan dibaca dengan PDF reader manapun

### Tiket PDF Features:
- ✅ **Structured Layout**: Header, informasi transaksi, detail film, kursi, dan footer
- ✅ **Professional Design**: Menggunakan fonts, tabel, dan line separators
- ✅ **Complete Information**: Semua detail transaksi dan jadwal
- ✅ **Print-Ready**: Format yang cocok untuk dicetak sebagai bukti pembayaran

### Laporan PDF Features:
- ✅ **Landscape Orientation**: Layout tabel yang optimal untuk banyak kolom
- ✅ **Table Headers**: Header kolom dengan background color
- ✅ **Data Formatting**: Format tanggal, harga, dan status yang konsisten
- ✅ **Summary Information**: Total jumlah transaksi dan timestamp generate

### PDF Endpoints:
- `GET /api/laporan/tiket/pdf/{transaksiId}` - Download tiket PDF individual
- `GET /api/laporan/pdf/transaksi` - Download laporan semua transaksi PDF

## Response Format Login

Setelah berhasil login, sistem akan mengembalikan response dalam format berikut:

```json
{
    "success": true,
    "message": "Login berhasil! Token akses telah dikirim ke email Anda.",
    "data": {
        "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
        "id": 1,
        "username": "admin_test",
        "email": "admin@test.com",
        "nomor": "08123456789",
        "tanggal_lahir": "1990-01-01",
        "status": true,
        "createdAt": "2025-10-03T13:52:02.951Z",
        "updatedAt": "2025-10-03T13:52:02.951Z",
        "role": "ADMIN",
        "expiresAt": "2025-10-03T14:52:02.951Z"
    }
}
```

**Catatan Penting:**
- `accessToken` sekarang langsung tersedia di response body
- Token juga tetap dikirim ke email user untuk referensi
- `expiresAt` menunjukkan kapan token akan expired
- Gunakan `accessToken` untuk autentikasi di endpoint lainnya

### User Endpoints (memerlukan role USER atau ADMIN)
- `GET /all/film/**` - Melihat daftar film (read-only)
- `GET /api/transaksi/checkout` - Checkout tiket
- `POST /api/transaksi/konfirmasi` - Konfirmasi pembayaran
- `POST /api/transaksi/cancel/{id}` - Batalkan transaksi
- `GET /api/transaksi/my-transactions` - Lihat transaksi sendiri
- `GET /api/transaksi/filter` - Filter transaksi sendiri
- `GET /api/transaksi/search` - Cari transaksi sendiri
- `GET /api/laporan/tiket/pdf/{id}` - Download tiket sendiri

### Admin Endpoints (hanya role ADMIN)
- `GET /users/all` - Lihat semua user
- `GET /users/search` - Cari user
- `GET /users/filter` - Filter user
- `POST /admin/**` - Semua endpoint admin untuk CRUD
- `GET /api/laporan/**` - Generate laporan

## Penggunaan AuthorizationUtil

```java
// Cek role pengguna
if (AuthorizationUtil.hasRole("ADMIN")) {
    // Logic khusus admin
}

// Validasi kepemilikan resource
AuthorizationUtil.validateOwnershipOrAdmin(resource.getUserId());

// Cek multiple roles
if (AuthorizationUtil.hasAnyRole("USER", "ADMIN")) {
    // Logic untuk user atau admin
}
```

## Error Response Format

Semua error otorisasi mengembalikan response dalam format:

```json
{
    "success": false,
    "message": "Pesan error yang deskriptif",
    "timestamp": "2025-10-03T13:52:02.951Z"
}
```

## Status Codes

- `401 Unauthorized` - Token tidak valid atau expired
- `403 Forbidden` - Tidak memiliki permission untuk akses endpoint
- `400 Bad Request` - Request tidak valid
- `500 Internal Server Error` - Error server

## Keamanan yang Diimplementasi

1. **JWT Token Security**
   - Validasi signature yang ketat
   - Pengecekan expiration otomatis
   - Error handling untuk token malformed

2. **Role-Based Access Control (RBAC)**
   - Validasi role di setiap endpoint
   - Pemisahan yang jelas antara USER dan ADMIN
   - Validasi kepemilikan resource

3. **Input Validation**
   - Validasi format token
   - Pengecekan null values
   - Sanitasi input

4. **Error Handling**
   - Error response yang informatif
   - Logging untuk audit trail
   - Tidak expose informasi sensitif

5. **Testing Coverage**
   - Test untuk semua skenario otorisasi
   - Test untuk edge cases
   - Test untuk role validation

## Catatan Penting

- Pastikan semua endpoint sudah memiliki `@PreAuthorize` annotation yang sesuai
- Gunakan `AuthorizationUtil` untuk validasi kompleks
- Monitor log untuk aktivitas mencurigakan
- Update token secara berkala untuk keamanan maksimal