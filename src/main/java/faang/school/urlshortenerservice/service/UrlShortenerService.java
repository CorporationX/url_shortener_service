package faang.school.urlshortenerservice.service;

public interface UrlShortenerService {
    String createShortUrl(String originalUrl);

    String getOriginalUrl(String shortUrl);
}
