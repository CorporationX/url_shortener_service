package faang.school.urlshortenerservice.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface UrlCacheRepository {
    void add(String url, String hash);
    String getUrl(String hash);
    String getHash(String url);
}

