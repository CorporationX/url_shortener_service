package faang.school.urlshortenerservice.config.snowflake;

import faang.school.urlshortenerservice.config.properties.UrlShortenerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.downgoon.snowflake.Snowflake;

@Configuration
@RequiredArgsConstructor
public class SnowflakeConfig {

    private final UrlShortenerProperties urlShortenerProperties;

    @Bean
    public Snowflake snowflake() {
        return new Snowflake(urlShortenerProperties.getClusterId(), urlShortenerProperties.getId());
    }
}
