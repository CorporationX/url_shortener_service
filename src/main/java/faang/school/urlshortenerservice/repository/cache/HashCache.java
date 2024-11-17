package faang.school.urlshortenerservice.repository.cache;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListMap;

@Component
@Data
@RequiredArgsConstructor
public class HashCache {
    private final ThreadPoolTaskExecutor hashCacheExecutor;
    private final ConcurrentSkipListMap<String, String> urlMap = new ConcurrentSkipListMap<>();

    @Value("${hash.maxSize}")
    private int maxSize;

    private void removeOldestUrl() {
        if (!urlMap.isEmpty()) {
            String oldestKey = urlMap.firstKey();
            urlMap.remove(oldestKey);
        }
    }

    public String getRandomShortUrl() {
        if (urlMap.isEmpty()) {
            return null;
        }

        List<String> keys = urlMap.keySet().stream().toList();
        Random random = new Random();
        return keys.get(random.nextInt(keys.size()));
    }
}
