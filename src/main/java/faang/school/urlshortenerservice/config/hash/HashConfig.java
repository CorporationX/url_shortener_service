package faang.school.urlshortenerservice.config.hash;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.hash")
@Getter
@Setter
public class HashConfig {

    private static final long PERCENT = 100;
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

    public long getCurrentOccupancyStorage() {
        return (long) ((double) storage.size * storage.refillPercentage / PERCENT);
    }

    public long getCurrentOccupancyCache() {
        return (long) ((double) cache.size * cache.refillPercentage / PERCENT);
    }
}
