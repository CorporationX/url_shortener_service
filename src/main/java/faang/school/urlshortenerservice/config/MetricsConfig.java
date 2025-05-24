package faang.school.urlshortenerservice.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для метрик Micrometer.
 * Этот класс регистрирует bean TimedAspect, который включает измерение времени выполнения методов,
 * аннотированных с помощью @Timed.
 */
@Configuration
public class MetricsConfig {

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}