package faang.school.urlshortenerservice.config.properties.hash;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "shortener.hash.cache.executor")
public record HashCacheExecutorProperties(
        @Positive int corePoolSize,
        @Positive int maxPoolSize,
        @Positive int queueCapacity
) {
    @AssertTrue(message = "maxPoolSize must be >= corePoolSize")
    public boolean isMaxPoolSizeValid() {
        return maxPoolSize >= corePoolSize;
    }
}
