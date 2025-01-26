package faang.school.urlshortenerservice.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class LocalCache {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.url-shortener.hash.cache-max-size}")
    private int hashMaxSize;

    public void saveHashesInCache(List<String> hashes) {
        for (String hash : hashes) {
            redisTemplate.opsForValue().set("hash_" + hash, hash);
        }
    }

    public String getHash() {
        Set<String> keys = redisTemplate.keys("*hash*");

        if (keys == null || keys.isEmpty()) {
            return null;
        }
        String[] keyArray = keys.toArray(new String[0]);
        String randomKey = keyArray[new Random().nextInt(keyArray.length)];

        return redisTemplate.opsForValue().get(randomKey);
    }

    public boolean hashSizeValidate() {
        long currentSize = getCacheSize();
        int threshold = (int) (hashMaxSize * 0.2);

        return currentSize > threshold;
    }

    private Long getCacheSize() {
        return redisTemplate
                .getConnectionFactory()
                .getConnection()
                .serverCommands()
                .dbSize();
    }
}
