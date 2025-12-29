package faang.school.urlshortenerservice.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import reactor.util.retry.Retry;

@Component
@Data
@ConfigurationProperties(prefix = "hash.batch-size")
public class HashGeneratorProperties {

    @Min(value = 1, message = "Batch size must be at least 1")
    @Max(value = 1000, message = "Batch size must not exceed 1000")
    private int batchSize = 100;

    @Valid
    private ThreadPool threadPool = new ThreadPool();

    @Valid
    private Retry retry = new Retry();

    @Data
    public static class ThreadPool {

        @Min(value = 1, message = "Thread pool size must be at least 1")
        @Max(value = 100, message = "Thread pool size must not exceed 100")
        private int size = 4;

        @Min(value = 100, message = "Queue capacity must be at least 100")
        @Max(value = 100000, message = "Queue capacity must not exceed 100000")
        private int queueCapacity = 10000;
    }

    @Data
    public static class Retry {

        @Min(value = 1, message = "Max attempts must be at least 1")
        @Max(value = 10, message = "Max attempts must not exceed 10")
        private int maxAttempts = 3;

        @Min(value = 100, message = "Delay must be at least 100ms")
        @Max(value = 60000, message = "Delay must not exceed 60000ms (60 сек)")
        private long delayMs = 1000;
    }
}
