package faang.school.urlshortenerservice.config.threadpool;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "properties")
@Validated
@Getter
@Setter
public class ThreadPoolProperties {
    @NotNull
    private Cache cache;

    @Getter
    @Setter
    public static class Cache {

        @Min(1)
        private int maxSize;

        @Min(1)
        @Max(100)
        private int refillThresholdPercent;

        @Min(1)
        private int batchSize;

        @NotNull
        private Executor executor;

        @Getter
        @Setter
        public static class Executor {
            @Min(1)
            private int poolSize;

            @Min(1)
            private int queueSize;
        }
    }
}