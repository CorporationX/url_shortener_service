package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCashRepository {

    private final RedisTemplate<String,String> redisTemplate;

    public void save (String kay, String value, long ttl){
        redisTemplate.opsForValue().set(kay,value,ttl, TimeUnit.HOURS);
    }

    public Optional<String> getValue (String kay){
        return Optional.ofNullable(redisTemplate.opsForValue().get(kay));
    }
}
