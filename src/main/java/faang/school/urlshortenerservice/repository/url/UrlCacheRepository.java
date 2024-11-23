package faang.school.urlshortenerservice.repository.url;

import faang.school.urlshortenerservice.entity.Url;

import java.util.Optional;

public interface UrlCacheRepository {

    void save(Url url);

    Optional<Url> findUrlByHash(String hash);
}
