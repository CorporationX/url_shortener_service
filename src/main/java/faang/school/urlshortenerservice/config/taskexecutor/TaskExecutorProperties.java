package faang.school.urlshortenerservice.config.taskexecutor;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "executor-service-properties")
public record TaskExecutorProperties(int corePoolSize,
                                     int maxPoolSize,
                                     int queueCapacity,
                                     int keepAliveTime,
                                     String threadNamePrefix) {
}
