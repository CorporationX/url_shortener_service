package faang.school.urlshortenerservice.repository;

public interface UrlCacheRepository {
    void save(String hash, String url);
    String get(String hash);
}
