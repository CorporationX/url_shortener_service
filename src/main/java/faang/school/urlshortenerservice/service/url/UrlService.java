package faang.school.urlshortenerservice.service.url;

public interface UrlService {

    String save(String url);

    String get(String hash);

    String getHash(String url);

    void deleteUnusedUrl();
}
