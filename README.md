# URL Shortener Service

A backend-focused URL shortener built with Spring Boot, created as a portfolio project to explore security, caching, rate limiting, and observability in a production-like setup.

The project focuses on system behavior, protection mechanisms, and metrics analysis rather than feature completeness or user-facing functionality.

---

## Motivation

This project was created to practice backend engineering concepts that commonly appear in public-facing services:

- secure handling of user-controlled URLs
- protection against common web vulnerabilities (XSS, SSRF, SQL injection)
- abuse prevention using rate limiting
- cache-first architecture for read-heavy workloads
- collecting and interpreting application and performance metrics

The goal was not to build a fully featured product, but to understand how such a system behaves under load and attack scenarios, and how to reason about its limitations.

---

## Key Technical Highlights

- URL validation and scheme filtering to prevent XSS attacks
- SSRF protection (localhost, private IP ranges, cloud metadata endpoints)
- Cache-first redirect flow using Redis
- Pre-generated hash pool to reduce latency during URL creation
- Per-user rate limiting using a token bucket algorithm (Bucket4j) via AOP
- Metrics exposure via Spring Boot Actuator and Micrometer
- Observability and performance analysis using Grafana

---

## Technology Stack

- **Language & Framework:** Java 17, Spring Boot 3.x
- **Persistence:** PostgreSQL
- **Caching & Coordination:** Redis
- **Security:** Custom URL validators (see [`UrlService`](src/main/java/faang/school/urlshortenerservice/service/UrlService.java) and [`RedirectValidator`](src/main/java/faang/school/urlshortenerservice/service/RedirectValidator.java)), prepared statements
- **Rate Limiting:** Bucket4j (token bucket algorithm) via [`RateLimitAspect`](src/main/java/faang/school/urlshortenerservice/aspect/RateLimitAspect.java)
- **Monitoring:** Micrometer, Spring Boot Actuator (see [`MetricsService`](src/main/java/faang/school/urlshortenerservice/service/MetricsService.java)), Grafana
- **Testing:** JUnit 5, k6 (load testing), Bash-based security tests

---

## How the System Works

**URL creation** follows a write-oriented flow:
- Input validation and normalization (see [`UrlService.validateRawUrl()`](src/main/java/faang/school/urlshortenerservice/service/UrlService.java))
- Security checks (XSS prevention in [`UrlService`](src/main/java/faang/school/urlshortenerservice/service/UrlService.java))
- Hash retrieval from a pre-generated pool (see [`HashCacheService`](src/main/java/faang/school/urlshortenerservice/service/HashCacheServiceRedis.java))
- Persistence in PostgreSQL (see [`UrlRepository`](src/main/java/faang/school/urlshortenerservice/repository/UrlRepository.java))
- Caching for fast future access (see [`UrlCacheRepository`](src/main/java/faang/school/urlshortenerservice/repository/UrlCacheRepository.java))

**Redirect handling** follows a read-heavy, cache-first approach:
- Lookup in Redis cache (see [`UrlCacheRepository`](src/main/java/faang/school/urlshortenerservice/repository/UrlCacheRepository.java))
- Fallback to database if cache miss occurs
- SSRF validation before redirect (see [`RedirectValidator`](src/main/java/faang/school/urlshortenerservice/service/RedirectValidator.java))
- HTTP 302 response with `Location` header

**Rate limiting** is applied via AOP aspect (see [`RateLimitAspect`](src/main/java/faang/school/urlshortenerservice/aspect/RateLimitAspect.java)) before request processing to protect the system from abuse and excessive request rates.

This separation allows efficient handling of redirects while keeping write operations controlled and observable.

**Core components:**
- [`UrlController`](src/main/java/faang/school/urlshortenerservice/controller/UrlController.java) - REST API endpoints
- [`RateLimitAspect`](src/main/java/faang/school/urlshortenerservice/aspect/RateLimitAspect.java) - AOP-based rate limiting with token bucket
- [`UrlService`](src/main/java/faang/school/urlshortenerservice/service/UrlService.java) - Business logic and URL validation (XSS prevention)
- [`RedirectValidator`](src/main/java/faang/school/urlshortenerservice/service/RedirectValidator.java) - SSRF protection and redirect safety
- [`HashCacheService`](src/main/java/faang/school/urlshortenerservice/service/HashCacheServiceRedis.java) - Hash pool management
- [`UrlRepository`](src/main/java/faang/school/urlshortenerservice/repository/UrlRepository.java) - Database operations
- [`UrlCacheRepository`](src/main/java/faang/school/urlshortenerservice/repository/UrlCacheRepository.java) - Redis cache operations

---

## Degradation & Resilience

The system is designed to degrade gracefully under partial failures rather than failing completely:

- **Redis as optimization, not dependency**  
  If Redis becomes unavailable or a cache miss occurs, redirect requests fall back to PostgreSQL (see [`UrlRepository`](src/main/java/faang/school/urlshortenerservice/repository/UrlRepository.java)). This increases latency but preserves functionality. The system remains operational even with complete Redis failure.

- **Controlled URL creation under pressure**  
  The pre-generated hash pool in Redis (see [`HashCacheServiceRedis`](src/main/java/faang/school/urlshortenerservice/service/HashCacheServiceRedis.java)) minimizes contention during normal operation. If the pool becomes temporarily depleted, the scheduler (see [`HashGeneratorScheduler`](src/main/java/faang/school/urlshortenerservice/scheduler/HashGeneratorScheduler.java)) refills it automatically. Rate limiting prevents exhaustion through excessive creation requests.

- **Rate limiting as protective degradation**  
  Excess traffic is rejected early with HTTP 429 responses via [`RateLimitAspect`](src/main/java/faang/school/urlshortenerservice/aspect/RateLimitAspect.java). This preserves system stability and prevents cascading failures while allowing legitimate traffic within limits to continue unaffected.

- **Early validation reduces downstream load**  
  Malformed or unsafe URLs are rejected in [`UrlService`](src/main/java/faang/school/urlshortenerservice/service/UrlService.java) before reaching persistence or caching layers. This limits resource usage during abusive request patterns or attack scenarios.

This approach prioritizes availability and predictable behavior over absolute throughput, which is appropriate for a service handling untrusted public input.

---

## API Endpoints

### Create Short URL

```http
POST /url
Content-Type: application/json

{
  "url": "https://example.com/very/long/path"
}
```

**Response:**
```http
201 Created
{
  "shortUrl": "http://localhost:8080/abc123"
}
```

### Redirect to Original URL

```http
GET /{hash}
```

**Response:**
```http
302 Found
Location: https://example.com/very/long/path
```

### API Documentation

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI spec: `http://localhost:8080/v3/api-docs`

---

## Documentation

- **[Architecture Documentation](docs/architecture-documentation.md)** - Comprehensive technical reference covering system architecture, component interactions, data flows, security mechanisms, performance characteristics, and scaling considerations.

## Testing & Analysis

The project includes a detailed testing report covering:

- Security testing (XSS, SSRF, SQL injection, rate limiting)
- Load testing with k6 (20 concurrent users, 11.5 minutes)
- Performance and latency analysis
- Cache behavior and hash pool stability
- Discussion of observed behavior and limitations

**Full testing report:**
- [PDF version](docs/COMPREHENSIVE%20TESTING%20REPORT%20URL.pdf) - Comprehensive testing report with detailed analysis

---

## Scope & Limitations

This project is intentionally scoped as a single-node, learning-oriented system:

- No horizontal scaling or load balancer
- Per-user rate limiting (chosen for simplicity and demonstration purposes, with anonymous fallback)
- IPv4-focused SSRF protection
- Local test environment without network latency simulation or TLS

These limitations and possible production improvements are discussed in detail in the [testing report](docs/COMPREHENSIVE%20TESTING%20REPORT%20URL.pdf) and [architecture documentation](docs/architecture-documentation.md).

---

## What I Learned

- How XSS and SSRF attacks apply specifically to URL-handling services
- How rate limiting affects system behavior under load
- Trade-offs of cache-first architectures
- How to interpret k6 load test results beyond raw success/failure rates
- How to use metrics to reason about performance and stability
- How to document system behavior and limitations clearly

---
