package faang.school.urlshortenerservice.executor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties(prefix = "executor.hash")
@Configuration
public class HashGeneratorExecutorConfig {

    private int corePoolSize;
    private int maxPoolSize;
    private String threadNamePrefix;
}