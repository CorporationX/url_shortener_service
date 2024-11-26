package faang.school.urlshortenerservice.config.properties;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Builder
@Component
@ConfigurationProperties(prefix = "app.url.life-time")
public class UrlLifeTimeConfig {
    private int months;
    private int days;
    private int hours;
}
