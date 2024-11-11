package faang.school.urlshortenerservice.config.database;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;


@Data
@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "spring.datasource")
public class DataBaseProperties {
    private String driverClassName;
    private String url;
    private String username;
    private String password;
}
