package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface UrlRepository {
    Optional<Url> findByUrl(String url);
    Optional<Url> findById(String hash);
    void save(Url url);
    List<String> deleteOldUrlsBefore(Timestamp cutoff);
}
