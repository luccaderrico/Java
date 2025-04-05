# Wallet Service

[![Heathcheck](http://localhost:7025/health)]
[![Swagger](http://localhost:7025/docs)]
[![Unit Test Coverage](http://localhost:63342/wallet/build/reports/jacoco/test/html/index.html)]
[![Arch Test Coverage](http://localhost:63342/wallet/build/reports/tests/archTest/index.html)]

## Requirements

* [Java 21](https://www.oracle.com/technetwork/java/javase/downloads/#java21)
* [Docker](https://www.docker.com/products/docker-desktop/)

## Development

### Data Dictionary

| Model            | Description                                   |
|------------------|-----------------------------------------------|
| Wallet           | Client`s wallet that manages user money       |
| Client           | Client representation                         |           
| Transaction      | Record of all debit and credit made by user   |           
| Balance          | Representation of user`s balance at some date |           
| BalanceSnapshot  | Snapshot of user`s balance                    |

### Setup

* Check if your jdk is configured in your`s environment variable Path.

### Build

```bash
./gradlew clean build
```

### Tests

#### Unit Tests
```bash
./gradlew test
```

#### Component Tests - (Base created - No Integration test yet)
```text 
./gradlew componentTest
```

#### Arch Tests
```bash
./gradlew archTest
```

### Before you run 📌
#### If you don`t have docker installed:
1. Download [Docker Desktop](https://www.docker.com/products/docker-desktop/).
2. Docker Compose is already included in Docker Desktop. If you're using Linux, you may need to install it manually.
3. Confirm installation:
   ```bash
   docker-compose version
   ```
4. Verify Everything is Running
   ```bash
    docker --version
    docker-compose --version
    docker ps
   ```

### Run Project 🚀

1. Run project`s containers from the project source directory (where docker-compose.yml is located) and run:
```bash
docker-compose up -d
```
2. Run the project
```bash
./gradlew run
```

<details>
<summary>How to test it? 🤖</summary>

1. Start creating client John (/create):
```curl
curl --location --request POST 'http://127.0.0.1:7025/api/v1/wallet/create' \
--header 'Content-Type: application/json' \
--header 'Authorization: service-secret' \
--header 'Accept: application/json' \
--data-raw '{
    "client": {
        "name": "John",
        "document_number": "824.775.240-97",
        "person_type": "PF"
    }
}'
```
2. Second create client Zara (/create):
```curl
curl --location --request POST 'http://127.0.0.1:7025/api/v1/wallet/create' \
--header 'Content-Type: application/json' \
--header 'Authorization: service-secret' \
--header 'Accept: application/json' \
--data-raw '{
    "client": {
        "name": "Zara",
        "document_number": "622.970.080-82",
        "person_type": "PF"
    }
}'
```
3. Now let\`s credit some money to John (/create):
```curl
curl --location --request POST 'http://127.0.0.1:7025/api/v1/wallet/credit' \
--header 'Content-Type: application/json' \
--header 'Authorization: service-secret' \
--header 'Accept: application/json' \
--data-raw '{
    "document_number": "82477524097",
    "amount": 10000.00
}'
```
4. Ok. Now John has enough money to be debited (/debit):
```curl
curl --location --request POST 'http://127.0.0.1:7025/api/v1/wallet/debit' \
--header 'Content-Type: application/json' \
--header 'Authorization: service-secret' \
--header 'Accept: application/json' \
--data-raw '{
    "document_number": "824.775.240-97",
    "amount": 100.00
}'
```
5. John can pay Zara the money he owed her (/transfer):
```curl
curl --location --request POST 'http://127.0.0.1:7025/api/v1/wallet/transfer' \
--header 'Content-Type: application/json' \
--header 'Authorization: service-secret' \
--header 'Accept: application/json' \
--data-raw '{
    "sender_document_number": "824.775.240-97",
    "recipient_document_number": "622.970.080-82",
    "amount": 100.00
}'
```
6. Finally, let\`s see John\`s current balance (/balance):
```curl
curl --location --request GET 'http://localhost:7025/api/v1/wallet/82477524097/balance' \
--header 'Authorization: service-secret'
```
7. Just for curiosity, what was John\`s balance after the first debit? (/balance?date):
```curl
curl --location --request GET 'http://localhost:7025/api/v1/wallet/82477524097/balance?date=<yyyy-mm-ddThh:mm:ss>' \
--header 'Authorization: service-secret'
```
</details>

<details>
<summary>Design Choices 🏛️</summary>

### Hexagonal
```text
    Also known as Ports and Adapters pattern, promotes flexibility, maintainability, and testability in software design.
By decoupling the core business logic from external dependencies (like databases and APIs in the project scenario), it 
enables easy adaption to new technologies and simplifies testing by allowing mock implementations. 
    This architecture fosters cleaner code organization, improves scalability, and enhances long-term maintainability,
making it ideal for complex and evolving applications.
```
### Traceability - Context Id
```text
    Traceability is crucial for debbuging, monitoring, and auditing, especially in distributed systems. Using context IDs
helps track a request across multiples services, ensuring consistency in logs. RequestContextFilter in Spring provides
a way to associate contextual data, like a trace or correlation ID, with a request, making it accessible throughout the
application lifecycle. When combined with SLF4J, it ensures structured and meaningful logging, improving observability.
    Choosing RequestContextFilter allows seamless propagation of contextual data without tightly coupling business logic 
to logging mechanisms, enhancing maintainability and traceability.
```
### Historical Balance Solution - Snapshots Routine
```text
    To efficiently support the Retrieve Historical Balance requirement, the database have a balance_snapshot table that
stores periodic balance records for each waller. A scheduled task using Spring`s @Scheduled annotation, with a cron
expression configurable via environment variable (initially set to run every midnight), will periodically capture snapshots.
    When retrieving a historical balance, the system first fetch the latest snapshot before the requested date. Then, it 
will apply all transactions from snapshot onward until reaching the specific date, ensuring accuracy without scanning
the entire transaction history. This hybrid approach balances performance and storage efficiency, reducing query overhead
while maintaining precision.
    Important to notice: In a distributed system, using @Scheduled alone can lead to multiple instances executing the same 
scheduled task simultaneously. If the service will run on a distributed system, @ShedLock ensures that only one instance 
executes the task at a time by leveraging a distributed lock mechanism, preventing data inconsistencies and race conditions.
    
```
</details>

<details>
<summary>Tradeoff`s ⚖️️</summary>

### Client Document Number As Identifier:
```text
    Due to time constraints, the decision was made to use the client’s document number as the wallet identifier, despite 
it being sensitive information. Ideally, an efficient database model would consider clients id`s, branches and accounts, 
but implementing this would require more code and additional development time.
    The document number, as a natural key, simplified the case study while acknowledging the sensitivity of using personal
identifiers in a real-world scenario. Use the document number as the wallet identifier introduced challenges, particularly in
querying wallets by document number, which could lead to joins and performance degradation.To mitigate this, combined indexes
were implemented to optimize queries, improving lookup efficiency.
```
### Security Config:
```text
    To simplify the security configuration, a secret key in the request header's Authorization field was used instead of a more 
robust solution like JWT. This approach was chosen to abstract the security layer, treating the secret key as a placeholder that,
in a real-world scenario, would be securely stored in an environment variable vault. 
    The correct implementation would involve creating a dedicated security library for the company, incorporating encryption 
and best practices. Additionally, security should be also enforced at the infrastructure level using AWS services such as VPCs, 
IAM roles, and security groups, all managed through Terraform to ensure a secure and scalable environment.
```

### Counterparty Absence:
```text
    Transfers were intentionally not registered to simplify the flow, as credits and debits already provide a sufficient 
basis for historical balance retrieval. When a transfer occurs, it is still represented in the transaction table through 
a debit in the sender’s wallet and a corresponding credit in the recipient’s wallet. 
    The settlement follows the credit and debit flow, ensuring that all transactions impacting the balance are properly 
recorded. The only missing detail is the explicit counterparty, which was omitted to avoid unnecessary complexity. This 
was a conscious decision to keep the data model simple while maintaining balance accuracy and consistency in historical 
queries, recognizing that in real-world scenarios, financial operations always have a registered counterparty.
```
</details>