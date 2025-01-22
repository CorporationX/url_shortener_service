package faang.school.urlshortenerservice.config.jdbc;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.datasource")
public record SpringJdbcProperties(
        String driverClassName,
        String url,
        String username,
        String password
) {
}
