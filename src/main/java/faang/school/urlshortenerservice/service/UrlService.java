package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import jakarta.servlet.http.HttpServletRequest;

public interface UrlService {
    String findOriginalUrl(String hash);
    UrlDto getShortUrl(UrlDto urlDto, HttpServletRequest httpServletRequest);
    void removeUnusedUrls();
}