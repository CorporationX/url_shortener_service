package faang.school.urlshortenerservice.repository;

import java.util.Optional;

public interface CacheRepository {
    void put(String key, String value);
    Optional<String> get(String key);
}
