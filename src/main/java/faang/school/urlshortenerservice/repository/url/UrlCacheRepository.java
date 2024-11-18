package faang.school.urlshortenerservice.repository.url;

import faang.school.urlshortenerservice.entity.url.Url;

public interface UrlCacheRepository {

    void save(Url url);

    String findUrlInCacheByHash(String hash);
}
