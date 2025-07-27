package faang.school.urlshortenerservice.service;

public interface UrlService {
    String shorten(String originalUrl);
    String getOriginal(String hash);
}
