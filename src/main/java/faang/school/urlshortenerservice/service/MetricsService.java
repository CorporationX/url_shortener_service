package faang.school.urlshortenerservice.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class MetricsService {

    private final MeterRegistry meterRegistry;

    public Counter urlCreationCounter() {
        return Counter.builder("url.creation.total")
                .description("Total number of URL creation requests")
                .register(meterRegistry);
    }

    public Counter urlCreationSuccessCounter() {
        return Counter.builder("url.creation.success")
                .description("Number of successful URL creations")
                .register(meterRegistry);
    }

    public Counter urlCreationFailureCounter() {
        return Counter.builder("url.creation.failure")
                .description("Number of failed URL creations")
                .tag("reason", "unknown")
                .register(meterRegistry);
    }

    public Counter urlCreationFailureCounter(String reason) {
        return Counter.builder("url.creation.failure")
                .description("Number of failed URL creations")
                .tag("reason", reason)
                .register(meterRegistry);
    }

    public Timer urlCreationTimer() {
        return Timer.builder("url.creation.duration")
                .description("Time taken to create a short URL")
                .register(meterRegistry);
    }

    public Counter urlRedirectCounter() {
        return Counter.builder("url.redirect.total")
                .description("Total number of redirect requests")
                .register(meterRegistry);
    }

    public Counter urlRedirectSuccessCounter() {
        return Counter.builder("url.redirect.success")
                .description("Number of successful redirects")
                .register(meterRegistry);
    }

    public Counter urlRedirectNotFoundCounter() {
        return Counter.builder("url.redirect.not_found")
                .description("Number of redirects where URL was not found")
                .register(meterRegistry);
    }

    public Timer urlRedirectTimer() {
        return Timer.builder("url.redirect.duration")
                .description("Time taken to process redirect")
                .register(meterRegistry);
    }

    public Counter hashCacheHitCounter() {
        return Counter.builder("hash.cache.hit")
                .description("Number of hash cache hits")
                .register(meterRegistry);
    }

    public Counter hashCacheMissCounter() {
        return Counter.builder("hash.cache.miss")
                .description("Number of hash cache misses")
                .register(meterRegistry);
    }

    public Counter hashCacheFallbackCounter() {
        return Counter.builder("hash.cache.fallback")
                .description("Number of fallbacks to database")
                .register(meterRegistry);
    }

    public Counter hashCacheReturnCounter() {
        return Counter.builder("hash.cache.return")
                .description("Number of hashes returned to cache")
                .register(meterRegistry);
    }

    public Counter hashGenerationCounter() {
        return Counter.builder("hash.generation.total")
                .description("Total number of hash generations")
                .register(meterRegistry);
    }

    public Counter hashGenerationOnTheFlyCounter() {
        return Counter.builder("hash.generation.on_the_fly")
                .description("Number of on-the-fly hash generations")
                .register(meterRegistry);
    }

    public Timer hashGenerationDurationTimer() {
        return Timer.builder("hash.generation.duration")
                .description("Time taken to generate a batch of hashes")
                .register(meterRegistry);
    }

    public Counter hashGenerationSuccessCounter(int batchSize) {
        return Counter.builder("hash.generation.success")
                .description("Number of successful hash batch generations")
                .tag("batch_size", String.valueOf(batchSize))
                .register(meterRegistry);
    }

    public Counter hashGenerationErrorCounter(String exceptionType) {
        return Counter.builder("hash.generation.error")
                .description("Number of hash generation errors")
                .tag("exception", exceptionType)
                .register(meterRegistry);
    }

    public Counter rateLimitExceededCounter() {
        return Counter.builder("rate.limit.exceeded")
                .description("Number of rate limit violations")
                .register(meterRegistry);
    }

    public Counter urlValidationFailureCounter(String reason) {
        return Counter.builder("url.validation.failure")
                .description("Number of URL validation failures")
                .tag("reason", reason)
                .register(meterRegistry);
    }

    public Counter redirectValidationFailureCounter(String reason) {
        return Counter.builder("redirect.validation.failure")
                .description("Number of redirect validation failures")
                .tag("reason", reason)
                .register(meterRegistry);
    }

    public Counter urlConflictCounter(String type) {
        return Counter.builder("url.conflict")
                .description("Number of URL conflicts")
                .tag("type", type) // "url" or "hash"
                .register(meterRegistry);
    }

    public Counter urlCacheHitCounter() {
        return Counter.builder("url.cache.hit")
                .description("Number of URL cache hits")
                .register(meterRegistry);
    }

    public Counter urlCacheMissCounter() {
        return Counter.builder("url.cache.miss")
                .description("Number of URL cache misses")
                .register(meterRegistry);
    }

    public void registerHashCacheSize(Supplier<Number> sizeSupplier) {
        Gauge.builder("hash.cache.size", sizeSupplier, supplier -> {
            try {
                Number size = supplier.get();
                return size != null ? size.doubleValue() : 0.0;
            } catch (Exception e) {
                return 0.0;
            }
        })
                .description("Current size of hash cache in Redis")
                .register(meterRegistry);
    }

    public void registerHashPoolSize(Supplier<Number> sizeSupplier, String type, String poolKey) {
        Gauge.builder("hash.pool.size", sizeSupplier, supplier -> {
            try {
                Number size = supplier.get();
                return size != null ? size.doubleValue() : 0.0;
            } catch (Exception e) {
                return 0.0;
            }
        })
                .description("Current size of hash pool in Redis")
                .tag("type", type)
                .tag("pool_key", poolKey)
                .register(meterRegistry);
    }
}

