package faang.school.urlshortenerservice.repository.url_cash;

import org.springframework.stereotype.Repository;

@Repository
public interface UrlCacheRepository {

    void saveUrl(String hash, String url);
    String getUrl(String hash);
}
