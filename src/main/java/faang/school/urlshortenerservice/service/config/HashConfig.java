package faang.school.urlshortenerservice.service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "hash")
@Getter
@Setter
public class HashConfig {
    private Storage storage = new Storage();
    private Cache cache = new Cache();

    @Getter
    @Setter
    public static class Storage {
        private int size;
        private double updatePercent;
    }

    @Getter
    @Setter
    public static class Cache {
        private int size;
        private double updatePercent;
    }

    public long getStorageUpdateCount() {
        return (long) ((double) storage.size * storage.updatePercent / 100);
    }

    public long getCacheUpdateCount() {
        return (long) ((double) cache.size * cache.updatePercent / 100);
    }
}