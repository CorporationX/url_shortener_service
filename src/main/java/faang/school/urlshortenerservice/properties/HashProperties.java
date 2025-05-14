package faang.school.urlshortenerservice.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@ConfigurationProperties(prefix = "hash")
@Validated
public class HashProperties {

    @NotNull
    private Cache cache;

    @NotNull
    private Generator generator;

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

    @Getter
    @Setter
    public static class Generator {

        @NotNull
        private Pool pool;

        @Min(1)
        private int queueSize;

        @Min(1)
        private int batchSize;

        @Getter
        @Setter
        public static class Pool {
            @Min(1)
            private int size;
        }
    }
}