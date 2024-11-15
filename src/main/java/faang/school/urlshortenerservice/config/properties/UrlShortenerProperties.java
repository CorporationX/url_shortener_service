package faang.school.urlshortenerservice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("server")
public class UrlShortenerProperties {
    private int clusterId;
    private int id;
}