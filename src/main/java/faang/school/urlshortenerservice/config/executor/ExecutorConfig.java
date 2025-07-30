package faang.school.urlshortenerservice.config.executor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app.executor.hash")
@Getter
@Setter
@Component
public class ExecutorConfig {
    private int core;
    private int max;
    private String prefix;
    private int queue;
}
