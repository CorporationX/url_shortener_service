package faang.school.urlshortenerservice.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "hash-generator.task-executor")
@RequiredArgsConstructor(onConstructor_ = @ConstructorBinding)
public class HashGeneratorAsyncConfig implements AsyncConfig {
    private final int corePoolSize;
    private final int maxPoolSize;
    private final int queueCapacity;
}
