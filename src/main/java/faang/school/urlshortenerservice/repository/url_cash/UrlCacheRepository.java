package faang.school.urlshortenerservice.repository.url_cash;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlCacheRepository {

    void saveUrl(String hash, String url);
    Optional<String> getUrl(String hash);
    void deleteUrl(String hash);
}
