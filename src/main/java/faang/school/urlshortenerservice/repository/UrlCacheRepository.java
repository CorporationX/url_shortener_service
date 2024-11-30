package faang.school.urlshortenerservice.repository;


import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class UrlCacheRepository {

    @CacheEvict("hash")
    public void clearCache(String hash) {}
}
