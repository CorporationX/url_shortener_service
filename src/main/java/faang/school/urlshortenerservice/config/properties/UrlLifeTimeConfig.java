package faang.school.urlshortenerservice.config.properties;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Builder
@ConfigurationProperties(prefix = "app.url.life-time")
public class UrlLifeTimeConfig {
    private int months;
    private int days;
    private int hours;
}
