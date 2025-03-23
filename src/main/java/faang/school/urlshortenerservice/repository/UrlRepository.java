package faang.school.urlshortenerservice.repository;

import java.util.Optional;

public interface UrlRepository {
    void save(String hash, String longUrl);

    Optional<String> findUrlByHash(String hash);
}
