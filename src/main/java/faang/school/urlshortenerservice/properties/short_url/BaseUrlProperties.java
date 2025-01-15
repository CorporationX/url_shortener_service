package faang.school.urlshortenerservice.properties.short_url;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "short-url.base")
@Component
public class BaseUrlProperties {

    private String domain;
    private String path;
}
