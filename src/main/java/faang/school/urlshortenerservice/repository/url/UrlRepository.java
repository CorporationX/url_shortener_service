package faang.school.urlshortenerservice.repository.url;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository {
    void save(String hash, String longUrl);
    Optional<String> findLongUrlByHash(String hash);
    List<String> retrieveAllUrlsElderOneYear();
}
