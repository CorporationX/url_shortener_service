package faang.school.urlshortenerservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "app.hash")
@Getter
@Setter
public class HashConfig {

    private Storage storage = new Storage();
    private Cache cache = new Cache();
    private int length;

    @Setter
    @Getter
    public static class Storage {
        private int size;
        private double refillPercentage;
    }

    @Setter
    @Getter
    public static class Cache {
        private int size;
        private double refillPercentage;
    }

    public long getCurrentStorageFullness() {
        return (long) ((double) storage.size * storage.refillPercentage / 100);
    }

    public long getCurrentCacheFullness() {
        return (long) ((double) cache.size * cache.refillPercentage / 100);
    }
}
