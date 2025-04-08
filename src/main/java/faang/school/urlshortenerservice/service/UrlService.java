package faang.school.urlshortenerservice.service;

public interface UrlService {
    String getUrlByHash(String hash);

    String getHashByUrl(String url);

    void deleteOldHashes();
}
