package faang.school.urlshortenerservice.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UrlCacheManager extends AbstractCacheManager<String> {
    private static final String TAG_FOR_CACHE = "HASH_URL";

    public UrlCacheManager(RedisTemplate redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    protected void addToCache(Url url) {
        super.addToCache(TAG_FOR_CACHE, Map.of(url.getHash(), url.getUrl()));
    }

    public Object getFromCache(String hash) {
        return super.getFromCache(TAG_FOR_CACHE, hash);
    }

    public void removeFromCache(String hash) {
        super.removeFromCache(TAG_FOR_CACHE, hash);
    }
}