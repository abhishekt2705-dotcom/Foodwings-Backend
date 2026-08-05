# FoodWings — Food Delivery Backend

A production-quality REST backend for a food delivery platform, built with **Java 21 + Spring Boot 3.3**.
It covers authentication, customers, restaurant owners, delivery partners and admins, with carts, orders,
payments, coupons, reviews, wishlists and notifications.

The project is layered (Controller → Service → Repository), uses the DTO pattern (entities are never
exposed directly), JWT-based role security, Bean Validation, global exception handling and Swagger/OpenAPI.

---

## Tech stack

| Area            | Technology                                   |
|-----------------|----------------------------------------------|
| Language        | Java 21                                       |
| Framework       | Spring Boot 3.3.4 (Web, Data JPA, Security)   |
| Persistence     | Hibernate / Spring Data JPA                   |
| Database        | MySQL (prod) · H2 in-memory (dev profile)     |
| Security        | Spring Security + JWT (JJWT 0.12.6), BCrypt   |
| Validation      | Jakarta Bean Validation                       |
| Docs            | springdoc-openapi (Swagger UI) 2.6.0          |
| Build           | Maven                                         |
| Boilerplate     | Lombok                                        |

---

## Quick start

### Prerequisites
- JDK 21
- Maven 3.6+
- (Optional) MySQL 8 for the production profile

### Run with the in-memory H2 database (no setup required)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
The app starts on **http://localhost:8080** and automatically seeds demo data.

### Run against MySQL (default profile)
Create the database (or let Hibernate create it) and start the app:
```bash
export DB_URL="jdbc:mysql://localhost:3306/foodwings?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export DB_USERNAME=root
export DB_PASSWORD=your_password
export JWT_SECRET="a-long-random-secret-of-at-least-64-bytes-for-HS512-signing-please"
mvn spring-boot:run
```

### Build a runnable jar
```bash
mvn clean package
java -jar target/foodwings.jar --spring.profiles.active=dev
```

---

## API documentation (Swagger)

Once running, open:
- Swagger UI: **http://localhost:8080/swagger-ui/index.html**
- OpenAPI JSON: **http://localhost:8080/v3/api-docs**

Click **Authorize** and paste `Bearer <accessToken>` (from `/api/auth/login`) to call secured endpoints.

A ready-to-import **Postman collection** is provided at [`postman/FoodWings.postman_collection.json`](postman/FoodWings.postman_collection.json).
It auto-captures the access token after login into the `{{token}}` variable.

---

## Seeded demo data & default credentials

On first startup (empty database) the app seeds:
- 10 categories, 10 restaurants (approved), 100 food items
- 20 customers, 10 delivery partners, 10 coupons
- Roles + an admin account

| Role              | Email                       | Password       |
|-------------------|-----------------------------|----------------|
| Admin             | `admin@foodwings.com`       | `Admin@123`    |
| Restaurant owner  | `owner1@foodwings.com` … `owner10@` | `Password@123` |
| Customer          | `customer1@foodwings.com` … `customer20@` | `Password@123` |
| Delivery partner  | `delivery1@foodwings.com` … `delivery10@` | `Password@123` |

Sample coupons: `WELCOME10`, `FOODWINGS15`, `SAVE20`, `FLAT50`, `PARTY25`, `WEEKEND30`,
`FIRSTBITE`, `TASTY12`, `MEGA40`, `HAPPY18` (each has a minimum order amount).

---

## Roles

`ADMIN`, `CUSTOMER`, `RESTAURANT_OWNER`, `DELIVERY_PARTNER` — enforced with JWT + Spring Security.

---

## Environment variables

| Variable                 | Default (dev)            | Description                          |
|--------------------------|--------------------------|--------------------------------------|
| `SPRING_PROFILES_ACTIVE` | `default` (MySQL)        | Use `dev` for H2                     |
| `DB_URL`                 | local MySQL foodwings    | JDBC URL                             |
| `DB_USERNAME`            | `root`                   | DB user                              |
| `DB_PASSWORD`            | `root`                   | DB password                          |
| `JWT_SECRET`             | dev fallback (override!) | HS512 signing secret (≥ 64 bytes)    |
| `SERVER_PORT`            | `8080`                   | HTTP port                            |

> ⚠️ The bundled `JWT_SECRET` is a development fallback only. **Always** set a strong secret in production.

---

## Project structure

```
com.foodwings
├── config          # SecurityConfig, WebConfig, OpenApiConfig, DataSeeder
├── controller      # REST controllers
├── dto
│   ├── request     # inbound request DTOs (validated)
│   └── response    # outbound response DTOs
├── entity          # JPA entities
├── enums           # RoleName, OrderStatus, PaymentMethod, ...
├── exception       # custom exceptions + GlobalExceptionHandler
├── mapper          # entity ↔ DTO mappers
├── repository      # Spring Data JPA repositories
├── response        # ApiResponse / PagedResponse envelopes
├── security        # JWT provider, filter, UserDetails, handlers
├── service         # service interfaces
│   └── impl        # service implementations
├── util            # constants, JWT helper
└── validation      # custom @Phone validator
uploads/            # runtime image storage (created automatically)
```

---

## API overview

All responses use a common envelope:
```json
{ "success": true, "message": "Operation Successful", "data": { }, "timestamp": "..." }
```

### Authentication — `/api/auth`
`POST /register` · `POST /login` · `POST /refresh` · `POST /logout` ·
`POST /forgot-password` · `POST /reset-password` · `POST /change-password`

### Public catalogue (no auth)
- `GET /api/restaurants`, `GET /api/restaurants/{id}`, `GET /api/restaurants/search?q=`
- `GET /api/restaurants/{id}/foods`, `GET /api/restaurants/{id}/reviews`
- `GET /api/foods`, `GET /api/foods/search?q=`, `GET /api/foods/{id}`, `GET /api/foods/best-sellers`, `GET /api/foods/popular`
- `GET /api/categories`, `GET /api/categories/{id}`

### Customer (auth required)
- Profile: `GET/PUT /api/profile`, `POST /api/profile/photo`
- Addresses: `GET/POST /api/profile/addresses`, `PUT/DELETE /api/profile/addresses/{id}`
- Cart: `GET /api/cart`, `POST /api/cart/add`, `PUT /api/cart/item`, `DELETE /api/cart/item/{foodItemId}`,
  `POST /api/cart/coupon`, `DELETE /api/cart/coupon`, `DELETE /api/cart`
- Orders: `POST /api/orders`, `GET /api/orders`, `GET /api/orders/{id}`, `PUT /api/orders/{id}/cancel`
- Reviews: `POST /api/reviews`, `GET /api/reviews/mine`
- Wishlist: `GET /api/wishlist`, `POST /api/wishlist/{restaurantId}`, `DELETE /api/wishlist/{restaurantId}`
- Notifications: `GET /api/notifications`, `GET /api/notifications/unread-count`,
  `PUT /api/notifications/{id}/read`, `PUT /api/notifications/read-all`

### Restaurant owner — `/api/restaurant` (`RESTAURANT_OWNER`/`ADMIN`)
- `POST /`, `PUT /{id}`, `DELETE /{id}`, `GET /mine`
- `POST /{id}/logo`, `POST /{id}/banner`, `GET /{id}/earnings`
- Menu: `POST /food`, `PUT /food/{id}`, `DELETE /food/{id}`, `POST /food/{id}/image`
- Orders: `GET /{id}/orders`, `PUT /order/{orderId}/status?status=ACCEPTED|PREPARING|READY|CANCELLED`

### Delivery partner — `/api/delivery` (`DELIVERY_PARTNER`/`ADMIN`)
- `GET /orders` (ready for pickup), `GET /history`
- `PUT /accept/{orderId}`, `PUT /reject/{orderId}`, `PUT /delivered/{orderId}`
- `PUT /status` (body: `{ "orderId", "status": "OUT_FOR_DELIVERY|DELIVERED" }`)

### Admin — `/api/admin` (`ADMIN`)
- `GET /dashboard`
- Users: `GET /users?role=`, `PUT /user/{id}/status?active=`, `DELETE /user/{id}`, `GET /delivery-partners`
- Restaurants: `GET /restaurants?status=`, `PUT /restaurant/{id}/approve`, `PUT /restaurant/{id}/reject`, `PUT /restaurant/{id}/status?active=`
- Categories: `POST /category`, `PUT /category/{id}`, `DELETE /category/{id}`
- Orders: `GET /orders`, `PUT /order/{id}/status?status=`
- Coupons: `GET /coupons`, `POST /coupon`, `DELETE /coupon/{id}`

---

## Order lifecycle

```
PLACED → ACCEPTED → PREPARING → READY → OUT_FOR_DELIVERY → DELIVERED
                                     └──────────► CANCELLED (before out-for-delivery)
```
Restaurant owners set `ACCEPTED/PREPARING/READY/CANCELLED`; delivery partners set
`OUT_FOR_DELIVERY/DELIVERED`; customers may cancel before the order is out for delivery.
Notifications are emitted on every transition and on payment success.

Payments: `CASH_ON_DELIVERY` starts `PENDING`; `UPI/DEBIT_CARD/CREDIT_CARD` are simulated as `SUCCESS`.

---

## Image uploads

Multipart uploads for restaurant logo/banner, food image and customer photo. Only image content types
(`png/jpeg/jpg/webp/gif`) are accepted; files are stored under `uploads/<subdir>/<uuid>.<ext>` and served
from `/uploads/**`. Only the relative path is stored in the database.

---

## Database schema (entities)

`User`, `Role` (M:N), `Address` (M:1 User), `Restaurant` (M:1 owner, 1:N foods), `Category`,
`FoodItem` (M:1 restaurant, M:1 category), `Cart` (1:1 User, 1:N CartItem), `CartItem`,
`Order` (M:1 customer/restaurant/delivery-partner, 1:N OrderItem, 1:1 Payment), `OrderItem`,
`Payment`, `Review` (M:1 User, optional restaurant/food), `Wishlist` (unique User+Restaurant),
`Coupon`, `DeliveryPartner` (1:1 User), `Notification`, `RefreshToken`, `PasswordResetToken`.

Tables are generated automatically by Hibernate (`ddl-auto=update` on MySQL, `create-drop` on H2).

---

## Deployment notes

1. Provision MySQL 8 and set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.
2. Set a strong `JWT_SECRET` (≥ 64 bytes).
3. Build: `mvn clean package` → `target/foodwings.jar`.
4. Run: `java -jar target/foodwings.jar` (default profile = MySQL).
5. Front a reverse proxy (Nginx) for TLS; persist the `uploads/` directory on a mounted volume.
6. Consider setting `spring.jpa.hibernate.ddl-auto=validate` with Flyway/Liquibase migrations in production.
