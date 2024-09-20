package faang.school.urlshortenerservice.cache;

import java.util.Optional;

public interface UrlCache {
    void saveUrl(String hash, String url);
    Optional<String> getUrl(String hash);
}
