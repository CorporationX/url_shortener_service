package faang.school.urlshortenerservice.config.cache;

import faang.school.urlshortenerservice.annotations.validation.cache.ValidTtl;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "spring.cache.caches")
@Data
@Validated
public class CacheProperties {

    private Cache urlCache;

    @Data
    @ValidTtl
    public static class Cache {

        @NotBlank
        private String prefix;

        private Long ttlInMinutes;
        private boolean ttlEnabled;
    }
}
