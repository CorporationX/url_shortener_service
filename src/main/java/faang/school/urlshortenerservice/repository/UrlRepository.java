package faang.school.urlshortenerservice.repository;

import java.util.List;
import java.util.Optional;

public interface UrlRepository {

    List<String> deleteOldUrlAndReturnHashes();

    void save(String url, String hash);

    Optional<String> getUrlByHash(String hash);
}
