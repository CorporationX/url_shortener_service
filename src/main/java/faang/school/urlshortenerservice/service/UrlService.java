package faang.school.urlshortenerservice.service;

public interface UrlService {

    String getFullUrl(String shortUrl);

    String createShortUrl(String fullUrl);
}
