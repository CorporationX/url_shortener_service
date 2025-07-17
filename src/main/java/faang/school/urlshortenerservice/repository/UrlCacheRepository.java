package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.dto.UrlDto;

public interface UrlCacheRepository {
    void set(String hash, UrlDto url, int ttl);
}
