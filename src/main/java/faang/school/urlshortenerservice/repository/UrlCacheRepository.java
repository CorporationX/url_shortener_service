package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;

import java.time.LocalDateTime;
import java.util.List;

public interface UrlCacheRepository {
    Url save(Url url);

    Url findByHash(String hash);

    List<Url> deleteAndReturnByCreatedAtBefore(LocalDateTime createdAt);
}
