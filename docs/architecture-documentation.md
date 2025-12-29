# URL Shortener - Architecture Documentation

## Document Purpose

This document provides a detailed architectural overview of the URL Shortener service, explaining design decisions, component interactions, data flows, and trade-offs. It serves as a technical reference for understanding system behavior and reasoning about scaling, resilience, and operational characteristics.

---

## Table of Contents

1. [System Overview](#system-overview)
2. [Architecture Principles](#architecture-principles)
3. [Component Architecture](#component-architecture)
4. [Data Flow](#data-flow)
5. [Storage Architecture](#storage-architecture)
6. [Security Architecture](#security-architecture)
7. [Performance Architecture](#performance-architecture)
8. [Operational Characteristics](#operational-characteristics)
9. [Design Trade-offs](#design-trade-offs)
10. [Scaling Considerations](#scaling-considerations)

---

## 1. System Overview

### Purpose

The URL Shortener is a backend service that converts long URLs into short, shareable links. The system prioritizes security, observability, and predictable behavior over feature richness or maximum throughput.

### Core Capabilities

- Create short URLs from arbitrary long URLs
- Redirect users from short URLs to original destinations
- Protect against common web vulnerabilities (XSS, SSRF, SQL injection)
- Prevent abuse through rate limiting
- Provide operational visibility through metrics

### Architecture Style

The system follows a **layered monolithic architecture** with clear separation of concerns:

- **Presentation Layer:** REST controllers
- **Security Layer:** Request filtering and validation
- **Business Logic Layer:** URL creation and retrieval
- **Data Access Layer:** Repository pattern with dual storage
- **Infrastructure Layer:** Caching and metrics

This architecture is appropriate for the system's scope and allows clear reasoning about request flow and component responsibilities.

---

## 2. Architecture Principles

### Fail-Safe Degradation

The system is designed to degrade gracefully rather than fail completely:

- **Redis failure:** System continues operating with PostgreSQL, accepting higher latency
- **Hash pool depletion:** Rate limiting prevents complete exhaustion
- **Validation errors:** Rejected early without affecting downstream components

### Defense in Depth

Security controls operate at multiple layers:

1. **Input layer:** URL scheme and format validation
2. **Business layer:** SSRF validation during redirect
3. **Data layer:** Parameterized queries prevent SQL injection
4. **Request layer:** Rate limiting prevents abuse

No single layer is relied upon exclusively for security.

### Cache-First Reads

Read-heavy operations (redirects) prioritize cache access:

- Redis checked first for fast path
- PostgreSQL used as fallback for reliability
- Cache misses trigger automatic cache population

This optimizes for the common case (95%+ cache hits) while maintaining correctness.

### Observable Behavior

System behavior is instrumented for operational visibility:

- Custom metrics for business operations (URL creation, redirects)
- Infrastructure metrics for resource usage (connections, memory)
- Standard HTTP metrics for request patterns

Metrics enable both real-time monitoring and post-incident analysis.

---

## 3. Component Architecture

### Component Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                        HTTP Layer                            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │            UrlController (REST API)                   │  │
│  │  - POST /url  (create short URL)                     │  │
│  │  - GET /{hash} (redirect to original)                │  │
│  │  - @RateLimited annotation on endpoints              │  │
│  └────────────────────┬─────────────────────────────────┘  │
└───────────────────────┼──────────────────────────────────────┘
                        │
┌───────────────────────▼──────────────────────────────────────┐
│                    Security Layer (AOP)                       │
│  ┌──────────────────────────────────────────────────────┐  │
│  │           RateLimitAspect (Spring AOP)                │  │
│  │  - Intercepts @RateLimited methods                   │  │
│  │  - Per-user token bucket (via x-user-id header)      │  │
│  │  - Falls back to "anonymous" if no user ID           │  │
│  │  - 10 requests/minute default (configurable)         │  │
│  └────────────────────┬─────────────────────────────────┘  │
└───────────────────────┼──────────────────────────────────────┘
                        │
┌───────────────────────▼──────────────────────────────────────┐
│                   Business Logic Layer                        │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                UrlService                             │  │
│  │  - URL creation orchestration                        │  │
│  │  - Duplicate detection                               │  │
│  │  - Cache management                                  │  │
│  │  - Integrated validation:                            │  │
│  │    • validateRawUrl() - XSS prevention               │  │
│  │    • validateNormalizedUrl() - format checks         │  │
│  └───┬──────────────────────────────────────┬───────────┘  │
│      │                                      │               │
│  ┌───▼──────────────────┐      ┌───────────▼───────────┐  │
│  │  HashCacheService    │      │  RedirectValidator    │  │
│  │  - Hash pool mgmt    │      │  - SSRF prevention    │  │
│  │  - Base62 encoding   │      │  - DNS resolution     │  │
│  │  - Scheduler refill  │      │  - IP validation      │  │
│  └───┬──────────────────┘      └───────────────────────┘  │
└──────┼───────────────────────────────────────────────────────┘
       │
┌──────▼───────────────────────────────────────────────────────┐
│                    Data Access Layer                          │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              UrlRepository                            │  │
│  │  - PreparedStatement queries                         │  │
│  │  - JDBC template usage                               │  │
│  └───┬──────────────────────────────────────┬───────────┘  │
└──────┼──────────────────────────────────────┼───────────────┘
       │                                      │
┌──────▼──────────┐                  ┌────────▼──────────┐
│   PostgreSQL    │                  │      Redis        │
│                 │                  │                   │
│ - url table     │                  │ - url cache       │
│ - hash index    │                  │ - hash pool       │
│ - persistent    │                  │ - volatile        │
└─────────────────┘                  └───────────────────┘
```

### Component Responsibilities

**UrlController**
- HTTP request/response handling
- Request validation (Jakarta Bean Validation)
- Response formatting
- OpenAPI documentation
- Annotated with `@RateLimited` for rate limiting

**RateLimitAspect**
- AOP-based rate limit enforcement
- Intercepts methods annotated with `@RateLimited`
- Per-user bucket management (via `x-user-id` header)
- Falls back to "anonymous" user for unauthenticated requests
- HTTP 429 response generation
- Retry-After header calculation

**UrlService**
- URL creation business logic
- Cache coordination (read/write)
- Duplicate URL detection
- Integrated URL validation:
  - `validateRawUrl()` - XSS prevention via scheme checking
  - `validateNormalizedUrl()` - format and structure validation
- Metrics recording

**HashCacheService**
- Pre-generated hash pool maintenance
- Base62 encoding/decoding
- Pool size monitoring
- Batch hash generation

**RedirectValidator**
- DNS resolution
- IP address validation
- SSRF prevention
- Private IP range detection

**UrlRepository**
- Database CRUD operations
- PreparedStatement usage
- Connection pool management
- Query optimization

**HashGeneratorScheduler**
- Automated pool refill
- Scheduled execution (configurable, default: hourly)
- Batch generation (100 hashes)
- Thread safety via ShedLock

---

## 4. Data Flow

### URL Creation Flow

```
1. Client Request
   POST /url {"url": "https://example.com/long/path"}
   Headers: x-user-id: user123 (optional)
   │
2. Rate Limit Check (AOP Aspect)
   │ ├─ Extract user ID from x-user-id header
   │ │  └─ Fallback to "anonymous" if header missing
   │ ├─ Check token bucket for user
   │ │  ├─ Token available → Continue
   │ │  └─ Token exhausted → 429 Too Many Requests
   │
3. URL Validation (UrlService)
   │ ├─ validateRawUrl(): Check URL scheme
   │ │  ├─ Valid scheme (http/https) → Continue
   │ │  └─ Invalid scheme (javascript/data/etc) → 400 Bad Request (XSS prevented)
   │ ├─ validateNormalizedUrl(): Format validation
   │
4. Duplicate Detection
   │ ├─ Check reverse cache: url → hash
   │ │  ├─ Hit → Return existing short URL
   │ │  └─ Miss → Continue
   │
5. Hash Allocation
   │ ├─ leftPop() hash from Redis List (hash:pool)
   │ └─ Pool empty → Generate on-demand (rare)
   │
6. Database Persistence
   │ ├─ INSERT INTO url (hash, url, created_at)
   │ └─ Commit transaction
   │
7. Cache Population
   │ ├─ Forward cache: hash → url (TTL: 24h)
   │ └─ Reverse cache: url → hash (TTL: 24h)
   │
8. Response
   └─ 201 Created {"shortUrl": "http://domain/abc"}
```

### Redirect Flow

```
1. Client Request
   GET /abc
   Headers: x-user-id: user123 (optional)
   │
2. Rate Limit Check (AOP Aspect)
   │ ├─ Extract user ID or use "anonymous"
   │ ├─ Token available → Continue
   │ └─ Token exhausted → 429 Too Many Requests
   │
3. Cache Lookup (Fast Path)
   │ ├─ Redis: GET url:abc
   │ │  ├─ Hit → url = "https://example.com/path"
   │ │  └─ Miss → Continue to database
   │
4. Database Fallback (Slow Path)
   │ ├─ SELECT url FROM url WHERE hash = 'abc'
   │ │  ├─ Found → url = "https://example.com/path"
   │ │  │         Cache population: SET url:abc
   │ │  └─ Not found → 404 Not Found
   │
5. SSRF Validation
   │ ├─ DNS resolution: example.com → 93.184.216.34
   │ ├─ IP validation: Public IP → Continue
   │ └─ Private IP → 400 Bad Request (SSRF prevented)
   │
6. Redirect Response
   └─ 302 Found
      Location: https://example.com/path
```

### Hash Pool Refill Flow

```
Scheduled Execution (Hourly, configurable)
   │
1. Check Pool Size
   │ ├─ LLEN hash:pool
   │ └─ Current: 850 hashes
   │
2. Calculate Refill Need
   │ ├─ Target: 1000
   │ └─ Needed: 150 (but refill batch is 100)
   │
3. Generate Hashes
   │ ├─ Generate 100 unique Base62 strings
   │ └─ Length: 3-6 characters
   │
4. Populate Pool
   │ ├─ rightPush() to hash:pool [hash1, hash2, ..., hash100]
   │ └─ New size: 950
   │
5. Log Operation
   └─ Scheduler logs refill completion
```

---

## 5. Storage Architecture

### Dual Storage Strategy

The system uses two storage technologies with complementary characteristics:

**PostgreSQL (Source of Truth)**
- Persistent storage
- ACID transactions
- Relational queries
- Backup and recovery

**Redis (Performance Layer)**
- Volatile cache
- Sub-millisecond latency
- List operations for hash pool
- TTL-based expiration

### Database Schema

**url table:**
```sql
CREATE TABLE url (
    hash VARCHAR(6) NOT NULL,
    url TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_url PRIMARY KEY (hash)
);

CREATE UNIQUE INDEX idx_hash ON url(hash);
CREATE UNIQUE INDEX idx_url_url_unique ON url(url);
```

**hash table:**
```sql
CREATE TABLE hash (
    hash VARCHAR(6) NOT NULL,
    available BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT pk_hash PRIMARY KEY (hash)
);

CREATE INDEX idx_hash_available ON hash(available) WHERE available = true;
```

**Design decisions:**
- `hash` is the primary key (VARCHAR(6)) for fast lookups during redirects
- `url` is indexed for duplicate detection (unique constraint via idx_url_url_unique)
- `created_at` enables time-based analytics and cleanup operations
- `hash` table stores pre-generated hashes with availability tracking
- No separate `id` field - hash serves as both identifier and primary key

### Redis Data Structures

**Forward cache (hash → url):**
```
Key: url:{hash}
Value: {original_url}
TTL: 24 hours
Type: String
Example: url:abc → "https://example.com/path"
```

**Reverse cache (url → hash):**
```
Key: url_to_hash:{url}
Value: {hash}
TTL: 24 hours
Type: String
Example: url_to_hash:https://example.com/path → "abc"
```

**Hash pool:**
```
Key: hash:pool
Value: List of unused hashes
TTL: None (persistent)
Type: List
Example: hash:pool → ["abc", "def", "ghi", ...]
Operations: 
  - leftPop() - atomic removal from head
  - rightPush() - batch addition to tail
  - size() - get pool size (LLEN)
```

### Cache Strategy

**Write-through pattern for creation:**
1. Write to PostgreSQL (source of truth)
2. Write to Redis cache (optimization)
3. Both must succeed for success response

**Cache-aside pattern for redirects:**
1. Check Redis cache
2. On miss, query PostgreSQL
3. Populate cache with result
4. Return to client

**Cache invalidation:**
- TTL-based expiration (24 hours)
- No manual invalidation (URLs are immutable)
- Cache warming on application startup (initial pool generation)

### Storage Trade-offs

**Why PostgreSQL:**
- Strong consistency guarantees
- Proven reliability for persistent data
- Familiar operational tooling
- ACID transactions

**Why Redis:**
- Extremely low latency (< 1ms typical)
- Built-in data structures (List for hash pool)
- Native TTL support
- High throughput for reads

**Why not single storage:**
- PostgreSQL alone: Higher latency for reads (10-50ms vs < 1ms)
- Redis alone: Data loss risk, no ACID guarantees
- Dual storage: Best of both, acceptable complexity

---

## 6. Security Architecture

### Threat Model

The system handles untrusted input from the public internet. Primary threats:

1. **Cross-Site Scripting (XSS):** Malicious JavaScript in URLs
2. **Server-Side Request Forgery (SSRF):** Access to internal networks
3. **SQL Injection:** Database manipulation through URL input
4. **Denial of Service:** Resource exhaustion through excessive requests
5. **Enumeration:** Discovering all shortened URLs

### XSS Prevention

**Attack vector:**
```
POST /url
{"url": "javascript:alert('XSS')"}
```

**Defense:**
- URL scheme validation integrated into `UrlService`
- `validateRawUrl()` method checks URL scheme
- Blacklist: `javascript:`, `data:`, `vbscript:`, `file:`, `about:`, `mailto:`, `tel:`
- Validation occurs before any storage
- Rejected with HTTP 400

**Implementation location:** `UrlService.validateRawUrl()` and `UrlService.validateNormalizedUrl()`

**Why this works:**
- Browsers execute certain URL schemes as code
- Blocking these schemes prevents execution
- Validation is fail-safe (whitelist would be better but more restrictive)
- Integrated into service layer for single point of control

### SSRF Prevention

**Attack vector:**
```
POST /url
{"url": "http://169.254.169.254/latest/meta-data"}
```

**Defense:**
- RedirectValidator resolves hostname to IP
- Checks IP against private ranges:
  - 127.0.0.0/8 (localhost)
  - 10.0.0.0/8 (private class A)
  - 172.16.0.0/12 (private class B)
  - 192.168.0.0/16 (private class C)
  - 169.254.0.0/16 (link-local, AWS metadata)
- Validation occurs at redirect time (not creation)
- Rejected with HTTP 400

**Implementation location:** `RedirectValidator.validateRedirectUrl()`

**Why redirect-time validation:**
- DNS responses can change (DNS rebinding attack)
- Creation-time validation can be bypassed
- Redirect-time validation is authoritative

**Known limitation:** IPv6 private ranges not validated

### SQL Injection Prevention

**Attack vector:**
```
POST /url
{"url": "https://example.com/'; DROP TABLE url;--"}
```

**Defense:**
- Exclusive use of PreparedStatement
- No string concatenation in SQL
- JDBC driver handles escaping
- SQL syntax in URL remains as literal data

**Implementation location:** `UrlRepository` all methods

**Why this works:**
- PreparedStatement separates SQL structure from data
- Database parses query before parameters bound
- User input cannot alter query structure

### Rate Limiting

**Attack vector:**
- Excessive URL creation → hash pool exhaustion
- Excessive redirects → database overload

**Defense:**
- Token bucket algorithm (Bucket4j)
- Per-user tracking via `x-user-id` header
- Falls back to "anonymous" user for requests without user ID
- AOP-based implementation using `@RateLimited` annotation
- Current limit: 10 requests/minute (configurable per endpoint)
- Returns HTTP 429 with Retry-After header

**Implementation location:** `RateLimitAspect`

**How it works:**
1. Aspect intercepts methods annotated with `@RateLimited`
2. Extracts user ID from `x-user-id` header
3. If no user ID, uses "rate-limit:anonymous" as key
4. Checks Bucket4j token bucket in Redis
5. Allows request if tokens available, rejects otherwise

**Why token bucket:**
- Allows burst traffic
- Smooth rate limiting
- Configurable capacity and refill rate
- Industry standard algorithm

**Known limitations:**
- Per-user requires `x-user-id` header (no IP-based fallback)
- Anonymous users share single bucket
- No distinction between authenticated/unauthenticated tiers
- Same rate limit for all endpoints (though configurable via annotation)

---

## 7. Performance Architecture

### Performance Goals

- Redirect latency p(95) < 200ms
- URL creation latency p(95) < 500ms
- Cache hit rate > 90%
- System stability during sustained load

### Cache Performance

**Observed metrics (load testing):**
- Cache hit rate: ~95-100%
- Cache lookup latency: < 5ms
- Cache miss penalty: +100-150ms (PostgreSQL query)

**Cache efficiency factors:**
- TTL: 24 hours (balances memory usage vs hit rate)
- Popular URLs naturally cached longer through repeated access
- Hash pool in Redis eliminates creation-time generation

### Database Performance

**Connection pool sizing:**
- Current: 50 connections (HikariCP)
- Minimum idle: 10 connections
- Observed concurrent usage: < 2
- Headroom: 25x current load

**Query optimization:**
- Indexed lookups on `hash` column
- PreparedStatement query plan caching
- Minimal joins (single table design)
- Unique constraint on `url` for duplicate detection

**Observed latency:**
- Hash lookup: 5-10ms
- URL insertion: 10-20ms

### Hash Pool Performance

**Design rationale:**
- Pre-generation eliminates creation-time encoding
- Redis List provides O(1) leftPop operation
- Batch refill (rightPush 100 hashes) reduces scheduler overhead
- FIFO behavior ensures even hash usage distribution

**Observed behavior:**
- Pool size: 974-1000 (stable)
- Pool never exhausted during testing
- Refill frequency: Hourly (configurable, sufficient for tested load)

### Bottleneck Analysis

**Current bottlenecks (under tested load):**
1. Rate limiting (intentional constraint)
2. DNS resolution for SSRF validation (50-100ms)

**Not bottlenecks:**
- Database connection pool (< 20% utilized)
- Redis connections (no contention observed)
- Hash pool (never depleted)
- Memory (no GC pressure)
- CPU (low utilization)

### Performance Trade-offs

**Cache vs Consistency:**
- Choice: 24h TTL with no invalidation
- Trade-off: Stale data possible (but URLs are immutable)
- Benefit: Simpler system, higher performance

**Validation timing:**
- Choice: SSRF validation at redirect time
- Trade-off: Increased redirect latency
- Benefit: Prevents DNS rebinding attacks

**Hash pool:**
- Choice: Pre-generation vs on-demand
- Trade-off: Memory usage, scheduler complexity
- Benefit: Consistent creation latency

---

## 8. Operational Characteristics

### Metrics & Observability

**Application metrics:**
```
url.creation.total           - Total URL creation attempts
url.creation.success         - Successful URL creations
url.creation.failure         - Failed URL creations (tagged by reason)
url.creation.duration        - Time taken to create a short URL
url.redirect.total           - Total redirect requests
url.redirect.success         - Successful redirects
url.redirect.not_found       - Redirects where URL was not found
url.redirect.duration        - Time taken to process redirect
url.cache.hit                - URL cache hits
url.cache.miss               - URL cache misses
url.validation.failure       - URL validation failures (tagged by reason)
redirect.validation.failure  - Redirect validation failures (tagged by reason)
url.conflict                 - URL conflicts (tagged by type: url or hash)
hash.cache.hit               - Hash pool cache hits
hash.cache.miss              - Hash pool cache misses
hash.cache.fallback          - Fallbacks to database for hash retrieval
hash.cache.return            - Hashes returned to cache
hash.cache.size              - Current size of hash cache (gauge)
hash.generation.total        - Total number of hash generations
hash.generation.on_the_fly   - On-the-fly hash generations
hash.pool.size               - Available hashes in pool (gauge)
rate.limit.exceeded          - Rate limit violations
```

**Infrastructure metrics:**
```
hikaricp.connections.active   - Active DB connections
hikaricp.connections.idle     - Idle DB connections
jvm.memory.used              - Heap usage
system.cpu.usage             - CPU utilization
```

**HTTP metrics:**
```
http.server.requests         - Request latency & count
http.server.requests.failed  - Error rate
```

### Failure Modes

**Redis unavailable:**
- Symptom: Cache misses increase to 100%
- Impact: Redirect latency increases to 100-150ms
- Mitigation: PostgreSQL fallback maintains functionality
- Recovery: Automatic when Redis restored

**PostgreSQL unavailable:**
- Symptom: URL creation fails, redirects fail on cache miss
- Impact: Service degraded (cached redirects work)
- Mitigation: None (PostgreSQL is source of truth)
- Recovery: Manual database restoration required

**Hash pool exhaustion:**
- Symptom: hash.pool.size → 0
- Impact: URL creation latency increases (on-demand generation)
- Mitigation: Rate limiting prevents complete exhaustion
- Recovery: Scheduler refills pool

**Rate limit misconfiguration:**
- Symptom: High rate of HTTP 429 responses
- Impact: Legitimate users blocked
- Mitigation: Configuration adjustment required
- Recovery: Immediate upon configuration change

### Resource Requirements

**Minimum configuration:**
- JVM: 512MB heap
- PostgreSQL: 1GB RAM, 10GB storage
- Redis: 256MB RAM

**Tested configuration:**
- Application: Default Spring Boot settings
- PostgreSQL: 50 connection pool (HikariCP)
- Redis: 8 connection pool

**Estimated capacity (tested load):**
- VU: 20 concurrent users
- Throughput: 4-5 req/s (with rate limiting)
- Latency p(95): 11ms overall
- Hash pool: Stable at 974-1000

---

## 9. Design Trade-offs

### Monolithic vs Microservices

**Decision:** Monolithic architecture

**Rationale:**
- Simpler deployment and operation
- No network calls between components
- Easier to reason about data flow
- Sufficient for current scope

**Trade-off:**
- Scaling requires scaling entire application
- Cannot scale components independently
- Single deployment unit

**When to reconsider:**
- Different scaling needs for read/write paths
- Team structure changes (multiple teams)
- Need for polyglot persistence

### Cache Consistency

**Decision:** TTL-based expiration, no invalidation

**Rationale:**
- URLs are immutable (never updated)
- TTL sufficient for use case
- Simpler than invalidation logic

**Trade-off:**
- Deleted URLs remain cached until TTL expires
- No way to force cache refresh

**When to reconsider:**
- URL editing feature added
- Compliance requirements for immediate deletion

### Hash Generation Strategy

**Decision:** Pre-generated pool with scheduled refill

**Rationale:**
- Consistent creation latency
- Avoids contention during concurrent creation
- Simple implementation

**Trade-off:**
- Memory usage for pool storage
- Scheduler adds complexity
- Pool can theoretically exhaust

**When to reconsider:**
- Memory constraints
- Pool exhaustion becomes common
- Need for guaranteed unique hashes

### Rate Limiting Granularity

**Decision:** Per-user (via `x-user-id` header) with anonymous fallback

**Rationale:**
- User-based limiting more accurate than IP-based
- Supports authenticated user scenarios
- Simple implementation with AOP
- Sufficient protection for demonstration

**Trade-off:**
- Requires `x-user-id` header for per-user limits
- Anonymous users share single bucket (can be overwhelmed)
- No IP-based fallback for header spoofing
- Read and write operations can have different limits (configurable via annotation)

**When to reconsider:**
- Add IP-based limiting as fallback
- Implement tiered access (free/premium)
- Add per-endpoint rate limits
- Implement distributed rate limiting across multiple instances

### Single Region Deployment

**Decision:** No geographic distribution

**Rationale:**
- Simpler operation
- Sufficient for learning project
- Avoids distributed systems complexity

**Trade-off:**
- Higher latency for distant users
- Single point of failure (datacenter)
- No disaster recovery

**When to reconsider:**
- Global user base
- Availability SLA requirements
- Compliance requirements (data residency)

---

## 10. Scaling Considerations

### Vertical Scaling (Single Node)

**Current headroom:**
- Database connections: 25x (50 configured, ~2 used)
- Redis connections: Sufficient
- Hash pool: Never depleted
- Memory: No pressure observed
- CPU: Low utilization

**Vertical scaling path:**
1. Increase rate limits (10 → 100 req/min)
2. Increase hash pool (1K → 10K)
3. Increase JVM heap (512MB → 2GB)
4. Connection pools already generous (50 DB, 8 Redis)

**Estimated capacity after vertical scaling:**
- 50-100 concurrent users
- 20-50 req/s sustained
- Latency p(95) < 50ms

**Vertical scaling limits:**
- Single database bottleneck
- Single Redis bottleneck
- No redundancy

### Horizontal Scaling (Multiple Nodes)

**Required changes:**

**1. Stateless application:**
- Current: Already stateless (no session state)
- No changes needed

**2. Load balancer:**
```
        Load Balancer (Nginx)
              │
      ┌───────┼───────┐
      │       │       │
   App1     App2    App3
      │       │       │
      └───────┼───────┘
              │
        ┌─────┴─────┐
    PostgreSQL    Redis
```

**3. Distributed rate limiting:**
- Current: Per-user buckets already in Redis (shared state)
- Change: No change needed (already distributed-ready)
- Impact: Rate limits correctly apply across all instances
- Note: Anonymous users share bucket across instances

**4. Hash pool coordination:**
- Current: Single Redis instance
- Change: Redis Cluster for high availability
- Impact: Pool shared across instances

**5. Database scaling:**
- Read replicas for redirect lookups
- Write master for URL creation
- Connection pool per application instance

### Read/Write Separation

Given redirect-heavy workload (reads >> writes):

**Read path optimization:**
- PostgreSQL read replicas (2-3 instances)
- Direct read traffic to replicas
- Cache misses hit replica, not master

**Write path:**
- Master handles all writes
- Replication lag acceptable (URLs immutable)

**Estimated improvement:**
- Read capacity: 3x
- Write capacity: 1x (still single master)
- Latency: Reduced (load distributed)

### Cache Scaling

**Redis scaling options:**

**1. Redis Cluster:**
- Multiple nodes with sharding
- Automatic failover
- Complexity: High

**2. Redis Sentinel:**
- Master-replica with automatic failover
- Simpler than Cluster
- No sharding

**3. Redis + CDN:**
- CDN for popular URLs
- Redis for long tail
- Complexity: Medium

**Recommendation:** Start with Sentinel for HA, add CDN if needed

### Bottleneck Evolution

**Current bottleneck:** Rate limiting (intentional)

**After rate limit increase:** Database connections

**After connection pool increase:** Database query performance

**After read replicas:** Write throughput (single master)

**After all optimizations:** Network bandwidth

### Cost vs Performance

**Current setup (single node):**
- Cost: Low (single server, single database)
- Performance: Sufficient for demo/learning

**Vertical scaling:**
- Cost: Medium (larger instances)
- Performance: 5-10x improvement
- Complexity: No change

**Horizontal scaling:**
- Cost: High (multiple instances, load balancer, replicas)
- Performance: 10-50x improvement
- Complexity: Significant increase

**Recommendation:** Vertical scaling first, horizontal scaling only when necessary

---

## Conclusion

This architecture prioritizes:

1. **Correctness:** Strong consistency through PostgreSQL
2. **Performance:** Cache-first for common case
3. **Security:** Defense in depth
4. **Observability:** Comprehensive metrics
5. **Simplicity:** Monolithic, single-region design

The system is intentionally scoped for learning and demonstration. Production deployment would require:

- Horizontal scaling for availability
- Distributed rate limiting (already supported)
- Redis clustering
- Database replication
- Monitoring and alerting
- Disaster recovery procedures

However, the core architecture is sound and the scaling path is clear.

