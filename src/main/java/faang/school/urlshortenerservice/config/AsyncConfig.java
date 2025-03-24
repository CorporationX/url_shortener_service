package faang.school.urlshortenerservice.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@EnableAsync
@Configuration
public class AsyncConfig {

    @Value("${shortener.executor.core-pool-size}")
    private int corePoolSize;

    @Value("${shortener.executor.max-pool-size}")
    private int maxPoolSize;

    @Value("${shortener.executor.queue-capacity}")
    private int queueCapacity;

    @Bean(name = "hashGenerator")
    public Executor hashGeneratorExecutor(MeterRegistry meterRegistry) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        Iterable<Tag> tags = List.of();
        new ExecutorServiceMetrics(
                executor,
                "hash_generator.executor",
                tags)
                .bindTo(meterRegistry);

        return executor;
    }
}
