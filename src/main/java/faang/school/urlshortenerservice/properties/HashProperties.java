package faang.school.urlshortenerservice.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private Caches caches;

    @Builder
    @Getter
    @Setter
    public static class Get {
        private int count;
        private int min;
        private int max;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Saving {
        private int minSize;
        private Duration time;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Caches {
        private String hashToUrl;
    }
}
