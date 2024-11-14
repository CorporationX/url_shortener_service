package faang.school.urlshortenerservice.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "hash")
public class HashProperties {

    private Batch batchValues;

    @Getter
    @Setter
    public static class Batch {

        private int save;
        private int get;
    }
}
