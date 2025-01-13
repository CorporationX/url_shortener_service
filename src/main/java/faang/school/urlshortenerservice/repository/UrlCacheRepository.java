package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final UrlRepository urlRepository;

    public String searchUrl(String hash) {
        String url = redisTemplate.opsForValue().get(hash);
        if (url == null) {
            Url urlFromDb = urlRepository.findById(hash).orElseThrow(() -> new EntityNotFoundException("Урл не найден"));
            url = urlFromDb.getUrl();
        }
        return url;
    }
}
