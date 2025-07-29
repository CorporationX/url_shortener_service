package faang.school.urlshortenerservice.cache;

public interface UrlHashCache {

    void put(String hash, String fullUrl);

    String get(String hash);
}