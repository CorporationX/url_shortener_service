package faang.school.urlshortenerservice.repository.interfaces;

import faang.school.urlshortenerservice.dto.UrlDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UrlRepository {
    List<String> deleteOlderThan(LocalDateTime threshold);

    Optional<UrlDto> findByHash(String hash);

    Optional<UrlDto> findByUrl(String url);

    void save(String hash, String url);
}