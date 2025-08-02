package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.ShortUrlDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.concurrent.CompletableFuture;

public interface UrlService {
    CompletableFuture<ShortUrlDto> createShortUrl(String longUrl, HttpServletRequest request);

    String getLongUrl(String shortUrl);

    void deleteOldUrls();
}
