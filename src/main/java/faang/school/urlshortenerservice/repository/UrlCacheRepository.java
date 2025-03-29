package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class UrlCacheRepository {
    private static final String URL_HASH = "urls:hash";
    private static final String URL_ZSET = "urls:zset";

    private final StringRedisTemplate stringRedisTemplate;
    private final int maxSize;;

    public String get(String hash){
        log.debug("Getting URL for hash: {}", hash);
        return stringRedisTemplate.<String, String>opsForHash().get(URL_HASH, hash);
    }

    public void set(String hash, String url){
        stringRedisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) {
                operations.multi();

                operations.opsForHash().put(URL_HASH, hash, url);
                operations.opsForZSet().add(URL_ZSET, hash, System.currentTimeMillis());

                Long zsetSize = operations.opsForZSet().size(URL_ZSET);
                if (zsetSize != null && zsetSize > maxSize) {
                    Set<String> oldHashes = operations.opsForZSet().range(URL_ZSET, 0, zsetSize - maxSize - 1);
                    operations.opsForZSet().removeRange(URL_ZSET, 0, zsetSize - maxSize - 1);
                    operations.opsForHash().delete(URL_HASH, oldHashes.toArray());
                }

                return operations.exec();
            }
        });
    }
}
