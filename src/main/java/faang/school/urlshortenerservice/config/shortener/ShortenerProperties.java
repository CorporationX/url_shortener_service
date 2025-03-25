package faang.school.urlshortenerservice.config.shortener;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "shortener")
public record ShortenerProperties(

        Integer hashesBatchSize,

        Integer multiplier,

        Integer queueSize,

        String encoder,

        Url url,

        String hashCron,

        String urlCron,

        Integer minArrayHashPercentage
) {

    public record Encoder(

            String codeBase,

            Integer mixParameter
    ) {
    }

    public record Url(

            Integer ttlDays,

            String prefix
    ) {
    }
}
