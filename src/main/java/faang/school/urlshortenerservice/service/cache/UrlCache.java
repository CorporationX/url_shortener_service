package faang.school.urlshortenerservice.service.cache;

import java.util.List;

public interface UrlCache {
    String get(String hash);

    void set(String hash, String url);

    void delete(String hash);

    void deleteAll(List<String> hashes);
}