package faang.school.urlshortenerservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cache")
public class CacheProperties {

    private int capacity = 5000;
    private int filPercent;
}
