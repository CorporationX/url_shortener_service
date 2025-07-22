package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlDto;

public interface UrlService {
    ShortUrlDto createShortUrl(UrlDto urlDto);
    UrlDto getUrl(ShortUrlDto shortUrlDto);
    void reuseOldUrls(int yearsCount);
    }