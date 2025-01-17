package faang.school.urlshortenerservice.config.thread.pool;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "thread-pool-task-executor")
public record ThreadPoolTaskExecutorPropertiesConfig(int corePoolSize,
                                                     int maxPoolSize,
                                                     int queueCapacity,
                                                     String threadNamePrefix) {
}
