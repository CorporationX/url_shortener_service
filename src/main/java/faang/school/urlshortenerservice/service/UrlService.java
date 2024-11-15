package faang.school.urlshortenerservice.service;

public interface UrlService {

    String getLongUrlByHash(String hash);

    String getShortUrlByHash(String url);
}
