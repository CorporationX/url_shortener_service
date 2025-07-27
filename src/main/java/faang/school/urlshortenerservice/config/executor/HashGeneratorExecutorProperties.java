package faang.school.urlshortenerservice.config.executor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "app.executor.hash")
@Getter
@Setter
@Configuration
public class HashGeneratorExecutorProperties {
    private int corePoolSize;
    private int maxPoolSize;
    private String threadNamePrefix;
}
