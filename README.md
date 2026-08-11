# Ecommerce API

Backend API for a simple ecommerce system with authentication, product catalog, shopping cart, orders and Kafka events for order status changes.

The project was developed using Spring Boot, PostgreSQL, Docker and Kafka. There is no frontend in this repository.

## Stack

- Java 25
- Spring Boot 4
- Spring Web MVC
- Spring Security
- OAuth2 Resource Server
- JWT with RSA keys
- Spring Data JPA
- PostgreSQL
- Apache Kafka
- Docker Compose
- Maven
- Lombok
- JUnit

## Features

- User registration and login
- JWT authentication with access and refresh tokens
- RSA key based JWT signing
- Admin and user roles
- Product creation, listing, editing and deletion
- Shopping cart item creation, listing, quantity update, selection toggle and deletion
- Order creation from selected cart items
- Order listing for the authenticated user
- Admin order listing with status filter
- Simulated payment, shipment and delivery flow
- Kafka producer and consumer for order lifecycle events
- Global exception handling
- Service layer tests

## Running Locally

Requirements: Java 25, Maven, Docker and OpenSSL.

Create JWT keys in `src/main/resources/keys`:

```bash
cd src/main/resources/keys
openssl genrsa -out jwt-private.key 4096
openssl rsa -in jwt-private.key -pubout -out jwt-public.key
```

The repository includes example key files:

```text
src/main/resources/keys/jwt-private.key.example
src/main/resources/keys/jwt-public.key.example
```

Start PostgreSQL and Kafka:

```bash
cd docker
docker compose up -d
```

Run the backend:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

API:

```text
http://localhost:8080
```

Local database configuration:

```text
Database: ecommerce_db
User: root
Password: root
PostgreSQL port: 5432
Kafka port: 9092
```

The application creates the roles automatically and also creates a test admin user:

```text
Email: admin@admin.com
Password: admin123
```

## API Overview

### Auth

- `POST /api/auth/register` - create account
- `POST /api/auth/login` - login and receive tokens
- `POST /api/auth/refresh` - refresh access token

### Products

- `GET /api/products?pageNumber=0&pageSize=20` - list products
- `GET /api/products/{id}` - find product by id
- `POST /api/products` - create product as admin
- `PATCH /api/products/{id}` - edit product as admin
- `DELETE /api/products/{id}` - delete product as admin

### Cart

- `GET /api/carts?pageNumber=0&pageSize=20` - list authenticated user's cart items
- `POST /api/carts` - add item to cart
- `PATCH /api/carts` - change cart item quantity
- `PATCH /api/carts/{cartItemId}` - toggle cart item selected
- `DELETE /api/carts/{cartItemId}` - delete cart item

### Orders

- `GET /api/orders/me?pageNumber=0&pageSize=10` - list authenticated user's orders
- `GET /api/orders?pageNumber=0&pageSize=10&orderStatus=PAID` - list orders as admin
- `POST /api/orders` - create order from selected cart items
- `POST /api/orders/simulate-payment/{id}` - simulate order payment
- `POST /api/orders/simulate-shipment` - simulate shipment as admin
- `POST /api/orders/simulate-delivered/{id}` - simulate delivery as admin
- `PATCH /api/orders` - move paid orders to preparing as admin
- `PATCH /api/orders/cancel/{id}` - cancel an unpaid order

## Kafka Events

The API publishes and consumes order events using Kafka topics:

- `order-created`
- `order-paid`
- `order-canceled`
- `order-expired`
- `order-to-preparing`
- `order-shipment`
- `order-delivered`

## Testing

```bash
./mvnw test
```

On Windows:

```bash
mvnw.cmd test
```

## Project Structure

```text
src/main/java/com/matheus/ecommerce
|-- application
|   |-- auth
|   |-- catalog
|   `-- sales
|-- domain
|   |-- auth
|   |-- catalog
|   `-- sales
|-- infrastructure
|   |-- database
|   |-- exception
|   |-- kafka
|   `-- security
`-- common

docker/     PostgreSQL and Kafka Docker Compose
```

## Future Improvements

- Add Swagger/OpenAPI documentation
- Integrate a real payment provider
- Consume a shipping carrier API
- Add order tracking updates
- Improve Kafka error handling and retry strategy
- Add integration tests with PostgreSQL and Kafka

## Author

Matheus R.M Silva  
GitHub: [MathSilvaDev](https://github.com/MathSilvaDev)
