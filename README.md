# Order Management System

System developed for TP5 - Block Project: Back-End Development

**Developed by:** Tiberius da Silva Dourado

## Description

This order management system allows users (customers, sellers and delivery persons) to place, track and manage orders in a simple and efficient way.

## Implemented Functional Requirements

- ✅ **RF01:** Customer registration/login
- ✅ **RF02:** Menu visualization
- ✅ **RF03:** Order registration
- ✅ **RF04:** Payment method registration
- ✅ **RF05:** Order status changes and visualization
- ✅ **RF06:** Order cancellation

## System Actors

- **Customer:** Places orders, manages addresses and payment methods
- **Seller:** Manages products, receives payments
- **DeliveryPerson:** Accepts and makes deliveries

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── ordermanagement/
│   │           ├── Main.java                 # Main CLI entry point
│   │           ├── ServerMain.java           # Server entry point
│   │           ├── api/                      # API layer
│   │           │   ├── HttpApiClient.java    # HTTP client implementation
│   │           │   ├── MockApiClient.java    # Mock API client for testing
│   │           │   └── MockApiServer.java    # Mock server implementation
│   │           ├── cli/                      # Command line interface
│   │           │   └── SimpleCLI.java        # CLI implementation
│   │           ├── dto/                      # Data transfer objects
│   │           │   ├── AddressDto.java
│   │           │   ├── CustomerDto.java
│   │           │   ├── OrderDto.java
│   │           │   ├── OrderItemDto.java
│   │           │   ├── PaymentCardDto.java
│   │           │   └── ProductDto.java
│   │           ├── model/                    # Domain entities
│   │           │   ├── User.java
│   │           │   ├── Customer.java
│   │           │   ├── Seller.java
│   │           │   ├── DeliveryPerson.java
│   │           │   ├── Product.java
│   │           │   ├── Order.java
│   │           │   ├── OrderItem.java
│   │           │   ├── Payment.java
│   │           │   ├── PaymentCard.java
│   │           │   ├── PaymentResult.java
│   │           │   └── Address.java
│   │           ├── repository/               # Data access interfaces
│   │           │   ├── UserRepository.java
│   │           │   ├── CustomerRepository.java
│   │           │   ├── SellerRepository.java
│   │           │   ├── DeliveryPersonRepository.java
│   │           │   ├── ProductRepository.java
│   │           │   ├── OrderRepository.java
│   │           │   └── PaymentRepository.java
│   │           ├── server/                   # Server components
│   │           │   └── JavalinServer.java    # Javalin REST API server
│   │           ├── service/                  # Business logic
│   │           │   ├── AuthService.java
│   │           │   ├── ProductService.java
│   │           │   ├── OrderService.java
│   │           │   ├── PaymentService.java
│   │           │   ├── DeliveryService.java
│   │           │   └── PaymentApiClient.java
│   │           ├── storage/                  # Data persistence
│   │           │   └── CsvDataManager.java   # CSV file storage
│   │           └── enums/                    # Enumerations
│   │               ├── OrderStatus.java
│   │               ├── PaymentMethod.java
│   │               ├── PaymentStatus.java
│   │               └── ProductCategory.java
│   └── resources/
└── test/
    └── java/
```

## Main Features

### Authentication and Registration

- Login for customers, sellers and delivery persons
- Registration of new users
- Credential validation

### Product Management

- Product registration by sellers
- Categorization (beverages, snacks, desserts)
- Availability control
- Search by name and category

### Order System

- Order creation by customers
- Adding items with observations
- Automatic calculation of subtotals and totals
- Status control (waiting, in preparation, on the way, delivered, cancelled)
- Order cancellation

### Payment Processing

- Support for multiple payment methods
- Integration with external payment API
- Failed payment retry
- Transfers to sellers and delivery persons

### Delivery System

- Automatic assignment of delivery persons
- Availability control
- Delivery tracking

## How to Run

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

### Compilation and Execution

```bash
# Compile the project
mvn clean compile

# Generate executable JAR
mvn clean package

# Run CLI mode (recommended)
java -jar target/order-management-system-1.0.0.jar

# Run REST API Server
java -cp target/order-management-system-1.0.0.jar com.ordermanagement.ServerMain

# Or run with Maven (may have terminal input issues)
mvn exec:java

# View system information
java -jar target/order-management-system-1.0.0.jar --info
```

### Run Tests

```bash
mvn test
```

## Architecture

The system implements a **layered architecture** with both CLI and REST API interfaces:

### Components

1. **CLI (Command Line Interface):**
   - Interactive user interface via SimpleCLI
   - Main menu and submenus for different operations
   - Data input and validation

2. **API Layer:**
   - **HttpApiClient:** Real HTTP client for production
   - **MockApiClient:** Simulated client for testing
   - **MockApiServer:** In-memory server simulation

3. **Server Components:**
   - **JavalinServer:** REST API server using Javalin framework
   - **ServerMain:** Server bootstrap and configuration

4. **DTOs (Data Transfer Objects):**
   - Clean data transfer between layers
   - JSON serialization with Jackson

5. **Persistence:**
   - **CsvDataManager:** CSV file-based data storage
   - Repository pattern for data access abstraction

### Data Flow

```
CLI → ApiClient → Server/MockServer → Services → Repositories → Storage
```

## Technologies and Patterns

### Technologies

- **Java 11:** Core language
- **Maven:** Build and dependency management
- **Javalin 5.6.3:** Lightweight REST API framework
- **Jackson:** JSON serialization/deserialization
- **JUnit 5:** Unit testing framework
- **SLF4J:** Logging facade

### Design Patterns

- **Layered Architecture:** Clear separation of concerns
- **Repository Pattern:** Data access abstraction
- **DTO Pattern:** Clean data transfer between layers
- **Mock Object Pattern:** Testing without external dependencies
- **Service Layer Pattern:** Business logic encapsulation

## Demonstration

### Available Features

1. **Registration and Login:**
   - Register new customer
   - Login with credentials

2. **Menu Visualization:**
   - View all available products
   - Detailed information (name, description, price, category)

3. **Place Orders:**
   - Create order with delivery address
   - Add multiple products
   - Choose payment method
   - Calculate total with delivery fee

4. **Manage Orders:**
   - View order history
   - Cancel orders (when allowed)
   - Track status in real time

### Pre-registered Products

- Artisanal X-Burger (R$ 25.90) - Maria's Snack Bar
- Margherita Pizza (R$ 35.00) - João's Pizzeria
- Cola Soda (R$ 5.50) - Maria's Snack Bar
- Milk Pudding (R$ 8.00) - Ana's Sweets Shop

### How to Test

1. Run: `java -jar target/order-management-system-1.0.0.jar`
2. Choose option "2" to register
3. Fill in your details
4. Place an order by choosing products from the menu
5. View your orders in the customer menu

## Contributions

This project was developed as part of TP5 - Block Project: Back-End Development.

---

**Author:** Tiberius da Silva Dourado
**Course:** EDS - Software Engineering Degree
