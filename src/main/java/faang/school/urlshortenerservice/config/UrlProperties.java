package faang.school.urlshortenerservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "spring.url")
@Getter
@Setter
public class UrlProperties {
    private Duration expirationPeriod;

    public String getExpirationInterval() {
        return DurationStyle
                .detect(this.expirationPeriod)
                .print(this.expirationPeriod);
    }
}

