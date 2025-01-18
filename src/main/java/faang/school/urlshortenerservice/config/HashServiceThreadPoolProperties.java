package faang.school.urlshortenerservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.async.hash")
public record HashServiceThreadPoolProperties(int corePoolSize,
                                              int maxPoolSize,
                                              int queueCapacity,
                                              String namePrefix) {}
