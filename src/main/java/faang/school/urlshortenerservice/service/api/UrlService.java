package faang.school.urlshortenerservice.service.api;

public interface UrlService {
    String generateShortUrl(String url);

    String getUrl(String hash);

    void cleaningExpiredUrls();
}
