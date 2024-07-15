package faang.school.urlshortenerservice.service.search.url;

import faang.school.urlshortenerservice.service.search.SearchesService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Order(1)
@RequiredArgsConstructor
public class RedisUrlService implements SearchesService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Optional<String> findUrl(String hash) {
        return Optional.ofNullable((String) redisTemplate.opsForValue().get(hash));
    }
}
