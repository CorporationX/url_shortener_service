package faang.school.urlshortenerservice.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "url.validation")
@Data
public class UrlValidationProperties {

    @Min(value = 100, message = "Max length must be at least 100")
    @Max(value = 10000, message = "Max length must not exceed 10000")
    private int maxLength = 2048;

    private List<String> forbiddenSchemes = List.of(
            "javascript", "data", "file", "about", 
            "vbscript", "mailto", "tel"
    );
}

