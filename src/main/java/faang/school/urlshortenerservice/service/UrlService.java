package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.ShortUrlDto;
import jakarta.servlet.http.HttpServletRequest;

public interface UrlService {
    ShortUrlDto createShortUrl(String longUrl, HttpServletRequest request);

    String getLongUrl(String shortUrl);

    void deleteShortUrl(String shortUrl);
}
