package faang.school.urlshortenerservice.config.url;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "url")
public class UrlConfig {

    private String shortName;
}
