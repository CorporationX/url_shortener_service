package faang.school.urlshortenerservice.cache;

public interface UrlCache {
    void saveUrl(String hash, String url);
}
