package faang.school.urlshortenerservice.config.redis;

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
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {

    @NotBlank
    private String host;

    @NotNull
    @Min(1)
    @Max(65535)
    private Integer port;

    @NotNull
    private Channels channels = new Channels();

    @Getter
    @Setter
    public static class Channels {

        @NotBlank
        private String requestStatusNotification;
    }
}
