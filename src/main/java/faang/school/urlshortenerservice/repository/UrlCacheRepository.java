package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.stereotype.Component;

@Component
public class UrlCacheRepository {
    public void save(Url url) {
        // TODO реализовать сохранение в Redis
    }

    public Url findByHash(String hash) {
        // TODO реализовать получение из Redis
        return null;
    }
}
