package faang.school.urlshortenerservice.cache.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.model.Url;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Evgenii Malkov
 */
@Service
@Slf4j
public class UrlCache extends AbstractCacheManager<String> {

    private static final String URL_CACHE_KEY = "URL_HASH";

    public UrlCache(ObjectMapper mapper, RedisTemplate<String, Object> redisTemplate) {
        super(mapper, redisTemplate);
    }

    public void put(Url url) {
        super.put(URL_CACHE_KEY, Map.of(url.getHash(), url.getUrl()));
    }

    public String get(String hash) {
        return super.get(URL_CACHE_KEY, hash);
    }

    public void remove(List<String> hashes) {
        super.delete(URL_CACHE_KEY, hashes);
    }
}
