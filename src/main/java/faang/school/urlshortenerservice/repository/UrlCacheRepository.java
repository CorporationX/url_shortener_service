package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;

import java.util.Optional;

public interface UrlCacheRepository  {
    void save(Url url);

    Optional<Url> findByHash(String hash);

    void deleteByHash(String hash);
}
