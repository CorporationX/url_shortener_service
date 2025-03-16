package faang.school.urlshortenerservice.config.shortener;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "shortener")
public record ShortenerProperties(

        Integer batchSize,

        Integer multiplier,

        Integer queueSize,

        String encoder,

        Url url,

        String cron,

        Integer minArrayHashPercentage
) {
    public record Url (

            Integer ttlDays,

            String prefix
    ) {}
}
