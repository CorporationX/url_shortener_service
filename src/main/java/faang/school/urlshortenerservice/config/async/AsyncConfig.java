package faang.school.urlshortenerservice.config.async;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class AsyncConfig {

    @Bean
    @ConfigurationProperties(prefix = "app.async.hash-generator")
    public HashGeneratorThreadPoolProperties hashGeneratorThreadPoolProperties() {
        return new HashGeneratorThreadPoolProperties();
    }

    @Bean(name = "hashGeneratorExecutor")
    public Executor hashGeneratorExecutor(HashGeneratorThreadPoolProperties properties) {
        return new ThreadPoolExecutor(
                properties.getCorePoolSize(),
                properties.getMaxPoolSize(),
                properties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(properties.getQueueCapacity()),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Getter
    @Setter
    public static class HashGeneratorThreadPoolProperties {
        private int corePoolSize = 2;
        private int maxPoolSize = 4;
        private int keepAliveTime = 60;
        private int queueCapacity = 100;
    }

    @Value("${spring.hash.queue-capacity:10000}")
    private int queueCapacity;

    @Bean
    public ArrayBlockingQueue<String> hashCashQueue() {
        return new ArrayBlockingQueue<>(queueCapacity);
    }
}
