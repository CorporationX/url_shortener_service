package faang.school.urlshortenerservice.repository.url;

import faang.school.urlshortenerservice.entity.url.Url;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface UrlCacheRepository {

    void save(Url url);

    Optional<Url> findUrlInCacheByHash(String hash);
}
