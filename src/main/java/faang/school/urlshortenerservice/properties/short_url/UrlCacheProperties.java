package faang.school.urlshortenerservice.properties.short_url;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "short-url.cache")
@Component
public class UrlCacheProperties {

    private int popularTtlHours;
    private int defaultTtlMinutes;
    private String defaultCacheName;
    private String popularCacheName;
    private int popularHashMaxCount;
}
