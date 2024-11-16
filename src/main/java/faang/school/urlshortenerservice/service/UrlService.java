package faang.school.urlshortenerservice.service;

public interface UrlService {
    String getUrlByHash(String hash);
    String createShortUrl(String originalUrl);
    void cleanUrl();
}
