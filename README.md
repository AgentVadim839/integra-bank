

# Integra Bank — Banking Core

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green)
![MySQL](https://img.shields.io/badge/MySQL-8-blue)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue)
![Architecture](https://img.shields.io/badge/Architecture-Monolithic-lightgrey)

**Integra Bank** is a fintech simulation designed to handle concurrent financial transactions with strict ACID compliance. The project focuses on data integrity, high availability during race conditions, and asynchronous processing of non-blocking tasks.

---

### Quick Access (Credentials)
https://integrabank.duckdns.org:8090/

| Role | Login (ID) | Password |
|------|------------|----------|
| USER | `1005` | `fun123` | 
| USER | `1007` | `fun123` |
| USER | `1008` | `fun123` |
| BANNED | `1009` | `fun123` |

Authentication is simplified for demo purposes. In production this would be HTTPS, JWT/OAuth2, password hashing, device/session tracking.

## Main Features

### 1. Concurrency & Data Integrity
* **Race Condition Protection**: Implemented Optimistic Locking (`@Version`) to prevent "Lost Update" anomalies during concurrent transfers.
* **Stable Transactions:** Integrated Retry Pattern (`Spring Retry`) to automatically recover from `OptimisticLockingFailureException`. The system retries the transaction up to 3 times with a 50ms backoff, ensuring a seamless user experience without data corruption. Any possible payment exceptions are being handled properly.
* **Decimal Precision:** All financial calculations use `BigDecimal` to avoid floating-point errors common in banking software.

### 2. Asynchronous Architecture
* **Non-blocking operations:** Heavy tasks (PDF receipt generation, Email notifications) are offloaded to a separate thread pool (`ThreadPoolTaskExecutor`) using Spring's `@Async` & Event-Driven approach.
* **Reliability:** Custom `AsyncManager` handles task execution logging and error management.

### 3. Security & Validation
* **Role-Based Access Control (RBAC):** Separate security chains for `ADMIN` and `USER` (Employee) roles.
* **Custom Security Filters:** Implemented `UserStatusInterceptor` to instantly invalidate sessions for banned users during active usage.
* **Advanced Validation:** Some of the business logic validation (balance checks, self-transfer prevention) decoupled from Controllers into dedicated Validator beans.

---

## Tech Stack

* **Core:** Java 17, Spring Boot 3
* **Data:** Spring Data JPA (Hibernate), MySQL
* **Security:** Spring Security (Form Login, BCrypt, Custom Handlers)
* **Reliability:** Spring Retry
* **Tools:** Docker, Docker Compose, Lombok, Maven, Slf4j
* **Reporting:** OpenPDF (iText), JavaMailSender

---

## How to Run

### 1. Clone the repository
	git clone [https://github.com/Pregnant-Cockroach/integra-bank.git]

### 2. Database Setup

The project requires a MySQL database. The database schema and initial data are exported as multiple SQL files located in the **`Integra Bank/mysql-dump`** directory.

1.  Create a database named `integra_bank`.
    
2.  Import **all** `.sql` files from the `mysql-dump` folder into this database.
    
    -   **Via MySQL Workbench:** Go to _Server -> Data Import_, select "Import from Self-Contained File" (or folder), and run the import.
        
    -   **Via Command Line:**
        

        
        ```
        cat "Integra Bank/mysql-dump/"*.sql | mysql -u root -p integra_bank
        ```

### 3. Configuration

Create `application.properties` in `src/main/resources/` (or rename the example file):

Properties

```
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/integra_bank
spring.datasource.username=your_user
spring.datasource.password=your_password

# Mail Configuration
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```
### 4. Build & Run
```
mvn spring-boot:run
```
The application will be available at: `http://localhost:8080`

### Demo credentials:
| Role | Login | Password | Desc |
|------|--------|---------|-----------|
| Admin | 1003 | fun123 | Admin user control dashboard |
| User | 1007 | fun123 | User dashboard | 

User:
http://localhost:8080/user/home

Admin:
http://localhost:8080/admin/home

## Architecture Insights

### Transaction Flow with Retry Pattern

The core `PaymentService` ensures reliability even under high concurrency:


```
@Transactional
@Retryable(
    retryFor = { ObjectOptimisticLockingFailureException.class },
    maxAttempts = 3,
    backoff = @Backoff(delay = 50)
)
public void makePayment(...) {
    // Business logic executed with atomic guarantees
}
```

### Async Task Management

PDF generation happens outside the main request thread to ensure fast response times:

Java

```
@Async("pdfGenerationExecutor")
public void generateReceiptAsync(String transactionId) {
    // PDF generation logic executed in a separate thread
}
```

## Author
### Designed & developed by [\[AgentVadim839\]](https://github.com/AgentVadim839)
