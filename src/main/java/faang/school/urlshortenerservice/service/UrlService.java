package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;

public interface UrlService {
    UrlDto shortenUrl(UrlDto dto);

    String getOriginalUrl(String hash);
}
