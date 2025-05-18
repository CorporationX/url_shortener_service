package faang.school.urlshortenerservice.config.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties {

        @NotBlank(message = "Redis host must not be blank")
        private String host;

        @NotNull(message = "Redis port must not be null")
        @Min(value = 1, message = "Redis port must be at least 1")
        @Max(value = 65535, message = "Redis port must not exceed 65535")
        private Integer port;

        @NotNull(message = "Redis timeout must not be null")
        @Min(value = 100, message = "Redis timeout must be at least 100ms")
        private Long timeout;

        @NotNull
        private Pool pool;

        @Getter
        @Setter
        public static class Pool {

                @NotNull
                @Min(1)
                private Integer maxTotal;

                @NotNull
                @Min(1)
                private Integer maxIdle;

                @NotNull
                @Min(1)
                private Integer minIdle;

                @NotNull
                @Min(1)
                private Integer maxWait;
        }
}

