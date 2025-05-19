package faang.school.urlshortenerservice.config.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "spring.hash")
public class HashProperties {

    @NotNull
    private Integer maxRange;

    @NotNull
    private Integer batchSize;
}

