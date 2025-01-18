package faang.school.urlshortenerservice.repository.url;

import org.springframework.stereotype.Repository;

@Repository
public interface UrlCacheRepository {
    void save(String hash, String longUrl);
    String find(String hash);
}
