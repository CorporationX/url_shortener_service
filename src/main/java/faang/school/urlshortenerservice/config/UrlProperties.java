package faang.school.urlshortenerservice.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.url")
@Data
public class UrlProperties {
    @NotBlank
    private String base;
}
