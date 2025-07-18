package faang.school.urlshortenerservice.repository;

import java.util.List;
import java.util.Optional;

public interface UrlRepository {
    void save(String hash, String url);
    Optional<String> findByHash(String hash);
    Long countExpired(String interval);
    List<String> getHashesAndDelete(String interval, int cleanUpBatchSize);
}
