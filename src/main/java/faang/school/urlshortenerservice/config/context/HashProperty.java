package faang.school.urlshortenerservice.config.context;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "service")
public record HashProperty(
        Integer numberOfHashes,
        Integer uniqueNumbersCount,
        Integer maxHashLength,
        Double minHashPercent
) {}
