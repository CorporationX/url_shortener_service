package faang.school.urlshortenerservice.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter failedSavedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("cache_saved_fails_total")
                .description("Number of failed cached saves")
                .register(meterRegistry);
    }

    @Bean
    public AtomicInteger hashCacheSizeGauge(MeterRegistry meterRegistry) {
        AtomicInteger gauge = new AtomicInteger(0);
        Gauge.builder("hash_cache_size", gauge, AtomicInteger::get)
                .description("Current number of free hashes in cache")
                .register(meterRegistry);
        return gauge;
    }
}
