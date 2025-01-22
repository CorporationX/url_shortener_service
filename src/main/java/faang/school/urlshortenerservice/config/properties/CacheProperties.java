package faang.school.urlshortenerservice.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Getter
@ConfigurationProperties(prefix = "app.cache")
public class CacheProperties {

    private final Map<String, Properties> names = new HashMap<>();

    @Getter
    @Setter
    public static class Properties {

        private Duration expire = Duration.ZERO;
    }
}
