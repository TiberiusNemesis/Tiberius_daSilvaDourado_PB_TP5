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
│   │           ├── Main.java                 # Main class
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
│   │           ├── service/                  # Business logic
│   │           │   ├── AuthService.java
│   │           │   ├── ProductService.java
│   │           │   ├── OrderService.java
│   │           │   ├── PaymentService.java
│   │           │   ├── DeliveryService.java
│   │           │   └── PaymentApiClient.java
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

# Run the JAR (recommended)
java -jar target/order-management-system-1.0.0.jar

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

The system implements a **Command Line Interface (CLI)** that interacts with a **simulated back-end** via **REST API**:

### Components:

1. **CLI (Command Line Interface):**
   - Interactive user interface
   - Main menu and submenus for different operations
   - Data input and validation

2. **DTOs (Data Transfer Objects):**
   - Objects for data transfer between client and server
   - JSON-friendly data representation

3. **API Client:**
   - Simulated client that abstracts HTTP calls
   - MockApiClient for demonstration without real server

4. **Mock Server:**
   - Complete back-end simulation
   - In-memory storage
   - Implemented business logic

### Data Flow:

```
CLI → MockApiClient → MockApiServer → DTOs
```

## Used Patterns

- **Client-Server Architecture:** Clear separation between client and server
- **DTO Pattern:** For structured data transfer
- **Mock Object Pattern:** For back-end simulation
- **Command Pattern:** For CLI operations
- **Factory Pattern:** For object creation

## Demonstration

### Available Features:

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

### Pre-registered Products:

- Artisanal X-Burger (R$ 25.90) - Maria's Snack Bar
- Margherita Pizza (R$ 35.00) - João's Pizzeria
- Cola Soda (R$ 5.50) - Maria's Snack Bar
- Milk Pudding (R$ 8.00) - Ana's Sweets Shop

### How to Test:

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