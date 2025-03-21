package faang.school.urlshortenerservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "spring.jpa.properties.hibernate.jdbc")
public class HashBatchProperties {
    private int batchSize;
}
