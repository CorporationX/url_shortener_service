package faang.school.urlshortenerservice.config.properties.url;

import faang.school.urlshortenerservice.config.properties.hash.HashProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "url")
public class UrlProperties {

    private TimeLimit timeLimit;

    @Getter
    @Setter
    public static class TimeLimit {

        private int year;
    }
}
