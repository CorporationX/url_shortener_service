package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;

public interface UrlService {

    UrlDto createShortUrl(UrlDto dto, String domain);

    UrlDto getOriginalUrl(String hash);
}
