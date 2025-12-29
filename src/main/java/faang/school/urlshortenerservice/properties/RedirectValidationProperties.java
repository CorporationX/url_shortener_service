package faang.school.urlshortenerservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "redirect.validation")
@Data
public class RedirectValidationProperties {

    private List<String> blacklistedDomains = new ArrayList<>();
}

