package faang.school.urlshortenerservice.repository.redis;

import faang.school.urlshortenerservice.dto.UrlDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, UrlDto> redisTemplate;


}
