package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final UrlRepository urlRepository;

    @CachePut(value = "url", key = "#url.hash")
    public Url save(Url url) {
        log.info("Сохранение в БД новой ссылки");
        return urlRepository.save(url);
    }

    @Cacheable(value = "url", key = "#hash")
    public Url find(String hash) {
        log.info("Запрос к базе данных для хэша: {}", hash);
        return urlRepository.findById(hash)
                .orElseThrow(() -> new EntityNotFoundException("Оригинальная ссылка не найден для хэша :" + hash));
    }
}
