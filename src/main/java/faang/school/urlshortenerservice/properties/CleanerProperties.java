package faang.school.urlshortenerservice.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@ConfigurationProperties(prefix = "cleaner")
@Validated
public class CleanerProperties {

    @NotBlank
    private String cron;
}