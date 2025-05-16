package faang.school.urlshortenerservice.service;

public interface UrlService {

    String save(String url);

    String get(String hash);

    void freeUnusedHash();
}
