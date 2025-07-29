package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;

public interface UrlCacheRepository {
    void save(String hash, Url url);

    Url get(String hash);
}