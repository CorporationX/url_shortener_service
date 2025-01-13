package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> template;
    private final UrlRepository urlRepository;

    public String searchUrl(String hash) {
        log.info("Начали поиск УРЛ в кеше ");
        String url = template.opsForValue().get(hash);
        if (url == null) {
            log.info("УРЛ не нашли в кеше, ищем в БД");
            Url urlFromDb = urlRepository.findById(hash).orElseThrow(() -> new EntityNotFoundException("Урл не найден"));
            url = urlFromDb.getUrl();
        }
        return url;
    }
}
