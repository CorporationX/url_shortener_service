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

    private BatchValues batchValues;

    @Getter
    @Setter
    public static class BatchValues {

        private int saveBatch;
        private int getBatch;
    }
}
