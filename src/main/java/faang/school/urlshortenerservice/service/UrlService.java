package faang.school.urlshortenerservice.service;

public interface UrlService {
    String getOriginalUrl(String hash);

    String createShortUrl(String originalUrl);
}
