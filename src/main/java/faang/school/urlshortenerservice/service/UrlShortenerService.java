package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.CreateUrlRequestDto;

public interface UrlShortenerService {
    void deleteOneYearOldUrl();

    String createShortUrl(CreateUrlRequestDto createUrlRequestDto);

    String getOriginalUrl(String hash);
}
