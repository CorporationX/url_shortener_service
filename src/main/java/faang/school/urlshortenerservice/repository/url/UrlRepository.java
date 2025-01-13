package faang.school.urlshortenerservice.repository.url;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository {
    void save(String hash, String longUrl);
    String findLongUrlByHash(String hash);
    List<String> retrieveAllUrlsElderOneYear();
}
