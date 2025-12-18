package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.UrlEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Repository;
import java.time.Duration;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redis;
    private static final String PREFIX_HASH = "hash:";
    private static final String PREFIX_URL = "url:";
    private static final Duration TTL = Duration.ofDays(1);

    public void cache(UrlEntity urlEntity) {
        try {
            redis.execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations operations) {
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
        } catch (DataAccessException e) {
            log.error("Failed to cache URL {} with hash {}", urlEntity.getOriginalUrl(), urlEntity.getHash(), e);
        }
    }

    public String getHashByOriginal(String url) {
        String key = PREFIX_HASH + url;
        String hash = redis.opsForValue().get(key);

        if (hash != null) {
            try {
                redis.execute(new SessionCallback<Object>() {
                    @Override
                    public Object execute(RedisOperations operations) {
                        RedisOperations<String, String> ops =
                                (RedisOperations<String, String>) operations;
                        ops.multi();
                        redis.expire(key, TTL);
                        redis.expire(PREFIX_URL + hash, TTL);
                        return ops.exec();
                    }
                });
            } catch (DataAccessException e) {
                log.warn("Failed to refresh TTL for URL {} / hash {}", url, hash, e);
            }
        }
        return hash;
    }

    public String getOriginalByHash(String hash) {
        String key = PREFIX_URL + hash;
        String url = redis.opsForValue().get(key);
        if (url != null) {
            try {
                redis.execute(new SessionCallback<Object>() {
                    @Override
                    public Object execute(RedisOperations operations) {
                        RedisOperations<String, String> ops =
                                (RedisOperations<String, String>) operations;
                        ops.multi();
                        redis.expire(key, TTL);
                        redis.expire(PREFIX_HASH + url, TTL);
                        return ops.exec();
                    }
                });
            } catch (DataAccessException e) {
                log.warn("Failed to refresh TTL for URL {} / hash {}", url, hash, e);
            }
        }
        return url;
    }
}