package faang.school.urlshortenerservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "url")
public class UrlProperties {

    private Cleaner cleaner;

    @Data
    public static class Cleaner {
        private String cron;
    }
}
