package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.ShortenedUrlDto;
import faang.school.urlshortenerservice.dto.UrlShortenerDto;

public interface UrlShortenerService {
    ShortenedUrlDto create(UrlShortenerDto urlShortenerDto);

    String findUrlByHash(String hash);

    void deleteCreatedAYearAgo();
}
