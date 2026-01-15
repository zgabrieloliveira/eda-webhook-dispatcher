# 🚀 Resilient Webhook Dispatcher

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3%2B-green)
![Kafka](https://img.shields.io/badge/Apache_Kafka-Kraft-black)
![Resilience4j](https://img.shields.io/badge/Pattern-Circuit_Breaker-red)

A robust, event-driven microservices architecture designed to dispatch webhooks with high availability and fault tolerance. This project demonstrates advanced backend patterns including **Circuit Breakers**, **Asynchronous Messaging**, and **Audit Logging**.

## 🏗️ Architecture

The system is composed of two main independent microservices:

1. **Producer API:** RESTful API that accepts webhook requests, validates payloads, and publishes events to a Kafka topic. It acts as a high-throughput ingestion layer.
2. **Consumer Worker:** A background worker that consumes events, manages delivery attempts, and handles failures using **Resilience4j**.

### Key Features

- **Event-Driven Communication:** Decoupled architecture using **Apache Kafka** (Kraft mode).
- **Resilience & Fault Tolerance:** Implements the **Circuit Breaker** pattern to prevent cascading failures when destination servers are down.
- **Strict Type Mapping:** Custom Jackson configuration to safely map decoupled DTOs between services.
- **Auditability:** Full delivery history (successes, failures, payloads, and retries) persisted in **PostgreSQL**.
- **Scalability:** Designed to run multiple consumer instances in the same Consumer Group.

## 🛠️ Tech Stack

- **Language:** Java 17
- **Framework:** Spring Boot (Web, Data JPA, Kafka)
- **Messaging:** Apache Kafka
- **Resilience:** Resilience4j (Circuit Breaker)
- **Database:** PostgreSQL
- **Containerization:** Docker & Docker Compose

## 🚀 Quick Start

### Prerequisites

- Java 17+
- Docker & Docker Compose
- Maven

### 1. Start Infrastructure

Run Kafka and Postgres containers:

```
docker-compose up -d
```

### 2. Run Services

Open two terminals to run the Producer and Consumer:

```bash
# Terminal 1: Producer API
mvn spring-boot:run -f producer-api/pom.xml
```

```bash
# Terminal 2: Consumer Worker
mvn spring-boot:run -f consumer-worker/pom.xml
```

## 3. How to Test

Get a URL from https://webhook.site

Dispatch an event using curl:

```bash
curl -X POST http://localhost:8081/webhooks \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": "portfolio-demo",
    "targetUrl": "YOUR_WEBHOOK_SITE_URL_HERE",
    "payload": {
      "message": "It works!",
      "status": "approved"
    }
  }'
```

Check the logs for:

```
✅ Event delivered successfully
```

