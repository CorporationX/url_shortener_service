//package faang.school.urlshortenerservice.repository;
//
//import org.springframework.data.redis.core.RedisTemplate;
//import java.util.HashMap;
//import java.util.Map;
//
//public class UrlCacheRepositoryMock extends UrlCacheRepository {
//    private final Map<String, String> cache = new HashMap<>();
//    public UrlCacheRepositoryMock(RedisTemplate<String, String> redisTemplate) {
//        super(redisTemplate);
//    }
//
//    @Override
//    public void save(String hash, String longUrl) {
//        cache.put(hash, longUrl);
//    }
//
//    @Override
//    public String get(String hash) {
//        return cache.get(hash);
//    }
//}
