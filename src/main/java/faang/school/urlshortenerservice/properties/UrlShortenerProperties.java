package faang.school.urlshortenerservice.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "url-shortener-service")
public class UrlShortenerProperties {
  private int hashBatchSize;
}