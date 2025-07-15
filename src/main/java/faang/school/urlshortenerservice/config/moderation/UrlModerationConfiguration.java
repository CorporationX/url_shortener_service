package faang.school.urlshortenerservice.config.moderation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "moderation.shortener")
@Getter
@Setter
@Component
public class UrlModerationConfiguration {
    private String cron;
}
