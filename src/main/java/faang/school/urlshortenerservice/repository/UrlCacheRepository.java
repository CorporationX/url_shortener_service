package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;

public interface UrlCacheRepository {

    void save(Url url);

    String findUrlByHash(String hash);
}
