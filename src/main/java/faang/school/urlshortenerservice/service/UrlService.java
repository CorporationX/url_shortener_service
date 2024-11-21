package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.UrlResponse;

public interface UrlService {
    UrlResponse generateShortUrl(UrlDto urlDto);
    UrlResponse getUrl(String shortUrl);
}
