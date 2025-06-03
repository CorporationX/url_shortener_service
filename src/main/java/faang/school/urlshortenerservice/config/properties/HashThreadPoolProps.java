package faang.school.urlshortenerservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hash.thread")
public record HashThreadPoolProps(Integer corePoolSize, Integer maxPoolSize, Integer queueCapacity) {
}
