package faang.school.urlshortenerservice.executor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "executor.hash")
@Component
public class HashGeneratorExecutorConfig {

    private int corePoolSize;
    private int maxPoolSize;
    private String threadNamePrefix;
}