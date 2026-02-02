package faang.school.urlshortenerservice.service;

public interface UrlService {

    String createShortUrl(String longUrl);

    String getOriginalUrl(String hash);
}
