package faang.school.urlshortenerservice.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "server")
public record UrlProperties(
        String pattern,
        int monthsToClearUrl
) {}
