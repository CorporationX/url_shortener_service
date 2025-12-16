package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.CreateShortUrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;

public interface UrlService {
    ShortUrlDto createShortUrl(CreateShortUrlDto createShortUrlDto);

    String getOriginalUrl(String shortUrl);
}
