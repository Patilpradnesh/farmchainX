# FarmchainX Backend — Implementation & Finalized Project Details

Purpose
- This file is the single authoritative backend implementation doc for developers and frontend integrators.
- It contains: the final API contract (paths, methods, request/response), fixed CORS/security rules, DB migration steps (to resolve `user_id` vs `id`), the prioritized code-fix list, standardized response format, test/smoke commands and recommended next steps.

How to use this file
- Backend engineers: implement the contract and fixes in the codebase, update Swagger and run the migration steps.
- Frontend engineers: use the exact endpoints and DTO shapes below; set axios baseURL to `http://localhost:8080` and include `Authorization: Bearer <token>` for protected endpoints.

---

CHECKLIST (what I implemented in this document)
- One authoritative API contract (method + path + request + response) for the system.
- Standardized response format (ApiResponse wrapper).
- Finalized enum lists for frontend usage.
- Concrete DB migration steps and safe SQL to remove legacy `user_id` column and ensure `users.id` is primary auto-increment key.
- Exact admin creation method (recommended API + SQL template + instructions to generate BCrypt hash).
- Code-level fixes checklist with exact code snippets to add/modify.
- CORS and Security confirmation and hardening guidance.
- Swagger/OpenAPI guidance and smoke-test curl examples.

---

SECTION A — API CONTRACT (authoritative)
- Base URL (dev): http://localhost:8080
- API prefixes:
  - /api -> auth/system endpoints
  - /api/v1 -> resource endpoints (crops, orders, analytics, marketplace, notifications, disputes)
- Auth: JWT bearer in header: `Authorization: Bearer <token>`

Global response wrapper (MUST be used everywhere)
- Standard response JSON: { success: boolean, message: string, data: any }
- Example success: { "success": true, "message": "OK", "data": { ... } }
- Example error:   { "success": false, "message": "Invalid request", "data": null }
- Controllers must return ResponseEntity<ApiResponse<T>>.

API list (core endpoints used by frontend)
- Auth
  - POST /api/auth/login
    - Request: { "email": string, "password": string }
    - Response 200: { success:true, message:"Login successful", data: { token: string, user: UserDto } }
  - POST /api/auth/register/{role}
    - Path role: ADMIN|FARMER|DISTRIBUTOR|RETAILER|CONSUMER
    - Request: { "email": string, "password": string, "name"?: string, "location"?: string }
    - Response 200: same as login (returns token + user)
  - GET /api/auth/me
    - Auth required. Response 200: { success:true, message:"OK", data: UserDto }

- Crops
  - POST /api/v1/crops/register
    - Request: CropCreateRequest { cropName, quantity, location, harvestDate (ISO LocalDateTime) }
    - Response 200: { success:true, message:"Crop registered", data: { id: number, hash: string } }
  - POST /api/v1/crops/add
    - Request body: CropRequest { name, certificatePath? } + query param `farmerEmail` (or prefer use of JWT identity)
    - Response 200: wrapper
  - GET /api/v1/crops/trace/{hash}
    - Response 200: { success:true, message:"OK", data: CropTraceResponse }

- Orders
  - POST /api/v1/orders
    - Request: OrderCreateRequest { cropId, requestedQuantity, offeredPrice, deliveryAddress, requestedDeliveryDate?, notes? }
    - Response 201: { success:true, message:"Order created", data: OrderDto }
  - GET /api/v1/orders/my
    - Auth required — list orders for logged-in user; Response 200: { success:true, message:"OK", data: [OrderDto,...] }
  - PUT /api/v1/orders/{orderId}/status
    - Request: { "status": "<ORDER_STATE>" }
    - Response 200: { success:true, message:"Status updated", data: OrderDto }
  - GET /api/v1/orders/{orderId}
    - Response 200: wrapper with OrderDto

- Marketplace
  - GET /api/v1/marketplace/crops
    - Query: page,size,search
    - Response 200: wrapper with list of crops
  - GET /api/v1/marketplace/search
  - POST /api/v1/marketplace/bid
    - Request: { cropId, bidAmount, quantity }

- Notifications
  - GET /api/v1/notifications/my
  - GET /api/v1/notifications/unread
  - PUT /api/v1/notifications/{notificationId}/read
  - PUT /api/v1/notifications/mark-all-read  (THIS is definitive endpoint for frontend for mark-all-read)

- Admin
  - POST /api/admin/approve/{userId}
    - Request: { "status": "APPROVED" | "REJECTED" }
    - Only ADMIN role allowed
    - Response 200: wrapper with updated UserDto
  - GET /api/admin/pending
  - GET /api/admin/users

- Analytics (stable shapes for charts)
  - GET /api/v1/analytics/users/by-role
    - Response 200: { success:true, message:"OK", data: [ { role: string, count: number }, ... ] }
  - GET /api/v1/analytics/crops/by-region
    - Response 200: { success:true, message:"OK", data: [ { region: string, cropName: string, count: number, totalQuantity: number }, ... ] }
  - GET /api/v1/analytics/orders/overview
    - Response 200: { success:true, message:"OK", data: { byRegion: [...], byBuyerRole: [...] } }

- Disputes
  - POST /api/v1/disputes { cropId, orderId?, description }
  - GET /api/v1/disputes
  - GET /api/v1/disputes/{disputeId}
  - PUT /api/v1/disputes/{disputeId}/resolve { action: RESOLVE|ESCALATE|REJECT, resolution: string }

- System
  - GET /api/system/endpoints — returns a map useful for frontend discovery and automated integration checks
  - GET /health

Notes: Add any newly added endpoints to Swagger immediately and keep this contract updated.

---

SECTION B — ENUMS (finalized values)
- UserRole: ADMIN | FARMER | DISTRIBUTOR | RETAILER | CONSUMER
- UserStatus: PENDING | APPROVED | REJECTED | SUSPENDED
- OrderState: PLACED | ACCEPTED | SHIPPED | COMPLETED | CANCELLED
- CropState: CREATED | LISTED | ORDERED | SHIPPED | DELIVERED | CLOSED
- DisputeStatus: OPEN | ESCALATED | RESOLVED | CLOSED | REJECTED
- DisputeAction: RESOLVE | ESCALATE | REJECT

Backend must only return these exact string values — frontend depends on them.

---

SECTION C — DATABASE MIGRATION (resolve users.id vs user_id)

Goal: remove legacy `user_id` column and ensure the table uses `id BIGINT AUTO_INCREMENT PRIMARY KEY`. This must be done carefully because of FK constraints.

Recommended safe sequence (MySQL Workbench / CLI)
1) Stop the Spring Boot application.
2) Connect to MySQL and inspect referencing FKs:
```sql
SELECT TABLE_SCHEMA, TABLE_NAME, COLUMN_NAME, CONSTRAINT_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE REFERENCED_TABLE_NAME = 'users';
```
3) If FKs reference `users.user_id`, record them. For each dependent foreign key do:
- Drop the FK constraint on the child table (record FK name from step 2):
```sql
ALTER TABLE child_table DROP FOREIGN KEY fk_name;
```
- If the child column is also `user_id` and correct, re-create the FK to `users(id)` after you fix `users` table. Example:
```sql
ALTER TABLE child_table ADD CONSTRAINT fk_child_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
```
4) Once no child FKs reference the legacy column, drop the legacy column from `users` (backup first):
```sql
ALTER TABLE users DROP COLUMN user_id;
```
5) Ensure `users.id` is primary auto-increment PK:
```sql
ALTER TABLE users MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY;
```
6) Recreate the child FKs to reference `users(id)` (if dropped earlier).

If schema is too inconsistent, recommended approach: run the supplied `database_complete_reset.sql` to recreate DB fresh (safer during development):
- Steps:
  SET FOREIGN_KEY_CHECKS = 0;
  DROP DATABASE IF EXISTS farmchainx_db;
  CREATE DATABASE farmchainx_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  USE farmchainx_db;
  -- paste full CREATE TABLE statements (from database_complete_reset.sql)
  SET FOREIGN_KEY_CHECKS = 1;

Important: back up existing data before destructive operations.

---

SECTION D — ADMIN USER CREATION (recommended safe options)
Preferred: create admin using API so password is encoded with the same BCrypt config used by the app:
```bash
curl -X POST "http://localhost:8080/api/auth/register/ADMIN" -H "Content-Type: application/json" -d '{"email":"admin@example.com","password":"admin@123"}'
```
If you must insert via SQL, generate a BCrypt hash using Node or Java and insert directly. Example Node snippet to generate a hash (run locally):
```js
// node -e "const bcrypt=require('bcrypt'); bcrypt.hash('admin@123',10).then(h=>console.log(h))"
```
Then run SQL (replace <BCryptHash>):
```sql
INSERT INTO users (email,password,role,status,created_at) VALUES
('admin@example.com', '<BCryptHash>', 'ADMIN', 'APPROVED', NOW());
```
Note: The app uses BCrypt; ensure cost/strength matches application `BCryptPasswordEncoder` (default 10) to avoid login issues.

---

SECTION E — CODE FIX LIST (priority order)
Fix these before attempting to start the app in dev. I can patch these if you give me permission.

1) Remove duplicate fields/getters/setters in entities (compile errors seen):
- Files to check/edit: `User.java`, `Order.java`, `Crop.java`.
  Action: keep one declaration per logical field. Remove duplicate method declarations like extra getUpdatedAt().

2) Fix `DataInitializer` runtime errors:
- Issue: references `FarmerProfile` or other profile entity classes that might be missing.
  Action: either restore the missing profile entity classes (FarmerProfile, DistributorProfile, RetailerProfile, ConsumerProfile) and their repositories, or change `DataInitializer` to conditional creation (check repository.isPresent() and guard save attempts).

3) Add missing repository method(s):
- `BlockchainRecordRepository` should declare:
```java
Optional<BlockchainRecord> findByTransactionHash(String transactionHash);
```

4) Handle checked exceptions in QRCodeService:
- Add throws clause or wrap ZXing `WriterException` in a runtime exception and convert to ApiResponse error.

5) Standardize controllers to return ApiResponse<T> wrapper. Add a small `ApiResponse` DTO and `@ControllerAdvice` to map exceptions.

6) Add or fix JwtAuthenticationFilter logic so it excludes `/api/auth/**` and permits OPTIONS (verified already but ensure no accidental changes).

7) Ensure `CorsConfig` explicitly allows frontend origins and that `SecurityConfig` calls `http.cors(...)` (already present). Keep `allowCredentials(false)` for header-JWT flows.

---

SECTION F — CODE SNIPPETS (drop-in helpers)

1) ApiResponse.java (put in `com.farmchainx.backend.common.dto`)
```java
public class ApiResponse<T> {
  private boolean success;
  private String message;
  private T data;
  // constructors, getters, setters omitted for brevity
  public static <T> ApiResponse<T> ok(String msg, T data) { return new ApiResponse<>(true, msg, data); }
  public static <T> ApiResponse<T> error(String msg) { return new ApiResponse<>(false, msg, null); }
}
```

2) Add repository method (example)
```java
public interface BlockchainRecordRepository extends JpaRepository<BlockchainRecord, Long> {
    Optional<BlockchainRecord> findByTransactionHash(String transactionHash);
}
```

3) Example controller pattern (admin approve)
```java
@PostMapping("/api/admin/approve/{userId}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ApiResponse<UserDto>> approveUser(@PathVariable Long userId, @RequestBody Map<String,String> body) {
    String status = body.get("status");
    // validate status
    UserDto updated = adminService.updateUserStatus(userId, status);
    return ResponseEntity.ok(ApiResponse.ok("User status updated", updated));
}
```

---

SECTION G — CORS & SECURITY (hardening)
- Keep `CorsConfig` explicit, no wildcard origins. Current allowed origins (dev): http://localhost:3000, http://localhost:3001, http://localhost:5173
- Keep `setAllowCredentials(false)` unless cookies/sessions are used.
- Keep `SecurityConfig` permit rules:
  - permitAll for `/api/auth/**`, `/api/system/**`, `/health`, `/actuator/health`
  - permitAll OPTIONS
  - `.requestMatchers("/api/admin/**").hasRole("ADMIN")`
  - `.anyRequest().authenticated()`

- Make sure JwtAuthenticationFilter is registered before UsernamePasswordAuthenticationFilter and excludes `/api/auth/**`.

---

SECTION H — SWAGGER / API DOCS
- Ensure `springdoc-openapi` is enabled and controllers/dtos have `@Operation` and `@Schema` where necessary.
- Confirm UI reachable at: http://localhost:8080/swagger-ui/index.html
- Confirm raw spec at: http://localhost:8080/v3/api-docs
- Keep Swagger up-to-date after any endpoint change and share URL with frontend team.

---

SECTION I — SMOKE TESTS (curl examples)
- Register admin (dev only):
  curl -X POST "http://localhost:8080/api/auth/register/ADMIN" -H "Content-Type: application/json" -d '{"email":"admin@example.com","password":"admin@123"}'

- Login (get token):
  curl -s -X POST "http://localhost:8080/api/auth/login" -H "Content-Type: application/json" -d '{"email":"admin@example.com","password":"admin@123"}' | jq .

- Admin approve (use admin token):
  curl -X POST "http://localhost:8080/api/admin/approve/9" -H "Authorization: Bearer <admin_token>" -H "Content-Type: application/json" -d '{"status":"APPROVED"}'

- Crop register (use farmer token):
  curl -X POST "http://localhost:8080/api/v1/crops/register" -H "Authorization: Bearer <farmer_token>" -H "Content-Type: application/json" -d '{"cropName":"Wheat","quantity":1000,"location":"Pune","harvestDate":"2026-02-15T10:00:00"}'

- Analytics (admin token):
  curl -X GET "http://localhost:8080/api/v1/analytics/users/by-role?timeFrame=month" -H "Authorization: Bearer <token>"

---

SECTION J — NEXT STEPS / OFFERED HELP
Pick one or more of the following and I will implement and validate:
A) I will apply code patches for the small compile-time issues (remove duplicate entity fields, add missing repository method, catch QR exceptions) and run `mvn -DskipTests package`; I will report build result and fix remaining errors.
B) I will produce the final admin SQL (single SQL line) with a BCrypt hash for `admin@123` and add it here (only SQL), if you want to insert directly.
C) I will generate a `client/lib/api.js` and scaffold three Recharts components wired to the analytics endpoints.
D) I will produce an ordered DB migration script that updates FKs, removes legacy user_id and ensures all FK constraints reference `users(id)` (non-destructive first, then destructive if you confirm backup).

Tell me which you want me to execute now. If you choose A or D I will make code edits and run build/migration checks; if B I will return only the SQL line for admin insertion.
