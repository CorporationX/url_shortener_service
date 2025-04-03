package faang.school.urlshortenerservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "url")
public class UrlProperties {

    private String baseShortUrl;
    private String retentionPeriod;
}
