package faang.school.urlshortenerservice.service;

public interface UrlService {

    String saveUrl(String url);

    String getUrl(String hash);

    String getHash(String url);

    void deleteUnusedUrl();
}
