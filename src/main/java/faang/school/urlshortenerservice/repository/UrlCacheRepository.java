package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.UrlEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Repository;
import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redis;
    private static final String PREFIX_HASH = "hash:";
    private static final String PREFIX_URL = "url:";
    private static final Duration TTL = Duration.ofDays(1);

    public void cache(UrlEntity urlEntity) {
        redis.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                RedisOperations<String, String> ops =
                        (RedisOperations<String, String>) operations;
                ops.multi();
                ops.opsForValue().set(PREFIX_HASH + urlEntity.getOriginalUrl(),
                        urlEntity.getHash(),
                        TTL);
                ops.opsForValue().set(PREFIX_URL + urlEntity.getHash(),
                        urlEntity.getOriginalUrl(),
                        TTL);
                return ops.exec();
            }
        });
    }

    public String getHashByOriginal(String url) {
        String key = PREFIX_HASH + url;
        String hash = redis.opsForValue().get(key);
        if (hash != null) {
            redis.expire(key, TTL);
            redis.expire(PREFIX_URL + hash, TTL);
        }
        return hash;
    }

    public String getOriginalByHash(String hash) {
        String key = PREFIX_URL + hash;
        String url = redis.opsForValue().get(key);
        if (url != null) {
            redis.expire(key, TTL);
            redis.expire(PREFIX_HASH + url, TTL);
        }
        return url;
    }
}