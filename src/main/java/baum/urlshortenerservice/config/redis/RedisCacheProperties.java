package baum.urlshortenerservice.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.util.Map;


@Data
@ConfigurationProperties(prefix = "spring.data.redis")
@ConfigurationPropertiesScan
public class RedisCacheProperties {
    private Map<String, String> caches;
}
