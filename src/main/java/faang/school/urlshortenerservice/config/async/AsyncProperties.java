package faang.school.urlshortenerservice.config.async;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Data
public class AsyncProperties {

    @Value("${async.executor.corePoolSize}")
    private int corePoolSize;

    @Value("${async.executor.maxPoolSize}")
    private int maxPoolSize;

    @Value("${async.executor.queueCapacity}")
    private int queueCapacity;

    @Value("${async.executor.threadNamePrefix}")
    private String threadNamePrefix;
}