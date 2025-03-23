package faang.school.urlshortenerservice.service;

public interface UrlCacheService {
    void saveUrl(String hash, String url);

    String getUrl(String hash);
}
