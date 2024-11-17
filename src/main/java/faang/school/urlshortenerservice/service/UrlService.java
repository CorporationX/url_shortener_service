package faang.school.urlshortenerservice.service;

public interface UrlService {

    String getLongUrl(String hash);

    String getShortUrl(String url);
}
