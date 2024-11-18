package faang.school.urlshortenerservice.service.url;

public interface UrlService {

    String getLongUrlByHash(String hash);

    String generateHashForUrl(String url);

    void cleaningOldHashes();
}
