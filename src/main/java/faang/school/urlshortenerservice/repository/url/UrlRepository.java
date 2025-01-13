package faang.school.urlshortenerservice.repository.url;

import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository {
    void save(String hash, String longUrl);
    String findLongUrlByHash(String hash);
}
