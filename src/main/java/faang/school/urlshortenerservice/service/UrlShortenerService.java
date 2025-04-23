package faang.school.urlshortenerservice.service;

public interface UrlShortenerService {
    String createShortenedUrl(String originalUrl);

    String getOriginalUrl(String shortUrl);
}
