package faang.school.urlshortenerservice.repository.cache;

import java.util.List;

public interface UrlCache {
    String get(String hash);
    void put(String hash, String url);
    void delete(List<String> hashes);
}
