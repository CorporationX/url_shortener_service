package faang.school.urlshortenerservice.config.executorservice;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@ConfigurationProperties(prefix = "executor-service-properties")
public record ExecutorServiceProperties(int corePoolSize, int maxPoolSize, long keepAliveTime, TimeUnit timeUnit) {
}
