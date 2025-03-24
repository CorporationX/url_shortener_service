package faang.school.urlshortenerservice.config.metric;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DatabaseAndRedisMetrics {

    private final MeterRegistry meterRegistry;
    private final JdbcTemplate jdbcTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${shortener.redis-free-hash-prefix}")
    private String freeHashPrefix;

    @Value("${shortener.redis-short-url-prefix}")
    private String shortUrlPrefix;

    public void registerMetrics() {
        Gauge.builder("db.url_mappings_count", () ->
                        jdbcTemplate.queryForObject("SELECT reltuples FROM pg_class WHERE relname = 'url_mappings'", Long.class))
                .description("The number of URL mappings in the database")
                .register(meterRegistry);

        Gauge.builder("db.free_hashes_count", () ->
                        jdbcTemplate.queryForObject("SELECT reltuples FROM pg_class WHERE relname = 'free_hashes'", Long.class))
                .description("The number of free hashes in the database")
                .register(meterRegistry);

        Gauge.builder("redis.short_url_count", () ->
                        redisTemplate.opsForHash().size(shortUrlPrefix))
                .description("The number of short URLs in Redis")
                .register(meterRegistry);

        Gauge.builder("redis.free_hash_count", () -> redisTemplate.opsForList().size(freeHashPrefix))
                .description("The number of free hashes in Redis")
                .register(meterRegistry);
    }
}
