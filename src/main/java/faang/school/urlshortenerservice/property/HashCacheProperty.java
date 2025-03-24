package faang.school.urlshortenerservice.property;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "hash.cache")
public class HashCacheProperty {
    private int size;
    private int lowPercent;
}
