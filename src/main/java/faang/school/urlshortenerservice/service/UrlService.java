package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;

public interface UrlService {
    UrlDto createShortUrl(UrlDto url);

    UrlDto getUrl(String hash);
}
