package faang.school.urlshortenerservice.config.hash;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cache")
public class HashProperties {

    private int capacity;
    private int fillPercent;
    private int expirationUrl;

}
