package faang.school.urlshortenerservice.config.async;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.executors")
public class ExecutorsProperties {

    private Map<String, PoolProps> pools = new HashMap<>();

    @Getter
    @Setter
    public static class PoolProps {
        private int corePoolSize;
        private int maxPoolSize;
        private int queueCapacity;
        private String prefix;
    }
}
