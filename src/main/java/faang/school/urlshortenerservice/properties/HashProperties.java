package faang.school.urlshortenerservice.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@ConfigurationProperties(prefix = "hash")
@Component
@Getter
@Setter
public class HashProperties {
    private int generateCount;
    private Get get;
    private Saving saving;

    @Getter
    @Setter
    public static class Get {
        private int count;
        private int min;
        private int max;
    }

    @Getter
    @Setter
    public static class Saving {
        private int minSize;
        private Duration time;
    }
}
