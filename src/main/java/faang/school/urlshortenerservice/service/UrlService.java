package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import jakarta.servlet.http.HttpServletRequest;

public interface UrlService {
    ShortUrlDto createShortUrl(UrlDto urlDto, HttpServletRequest httpServletRequest);

    UrlDto getUrl(String hash);

    void reuseOldUrls(int daysCount);
}