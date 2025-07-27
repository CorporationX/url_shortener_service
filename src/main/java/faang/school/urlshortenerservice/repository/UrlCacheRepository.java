package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.dto.UrlResponseDto;

public interface UrlCacheRepository {
    void set(String hash, UrlResponseDto url);

    UrlResponseDto get(String hash);
}
