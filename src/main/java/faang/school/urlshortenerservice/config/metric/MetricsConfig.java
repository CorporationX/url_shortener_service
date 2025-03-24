package faang.school.urlshortenerservice.config.metric;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class MetricsConfig {

    private final DatabaseAndRedisMetrics databaseAndRedisMetrics;

    @Bean
    public void registerMetrics() {
        databaseAndRedisMetrics.registerMetrics();
    }
}
