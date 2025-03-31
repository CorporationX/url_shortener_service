package faang.school.urlshortenerservice.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UrlCacheRepository {

    @CachePut(value = "url", key = "#hash")
    public String save(String hash, String url) {
        log.info("Ссылка сохранена в кэш: hash={}, url={}", hash, url);
        return url;
    }

    @Cacheable(value = "url", key = "#hash")
    public String find(String hash) {
        log.info("Ссылка не найдена в кэше: hash={}", hash);
        return null;
    }
}
