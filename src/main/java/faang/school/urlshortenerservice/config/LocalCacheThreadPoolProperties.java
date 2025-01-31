package faang.school.urlshortenerservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.async.local-cache")
public record LocalCacheThreadPoolProperties(int corePoolSize,
                                             int maxPoolSize,
                                             String namePrefix) {
}
