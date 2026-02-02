package faang.school.urlshortenerservice.repository;

import java.util.Optional;

public interface UrlCacheRepository {

    Optional<String> getUrl(String hash);

    void save(String hash, String url);
}
