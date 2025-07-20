package faang.school.urlshortenerservice.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter failedSavedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("cache_saved_fails_total")
                .description("Number of failed cached saves")
                .register(meterRegistry);
    }
}
