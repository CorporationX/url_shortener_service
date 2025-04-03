package faang.school.urlshortenerservice.repository.api;

import java.util.List;
import java.util.Optional;

public interface CacheRepository {

    void save(String hash, String url);

    Optional<String> findByHash(String hash);

    void removeHashes(List<String> hashes);
}
