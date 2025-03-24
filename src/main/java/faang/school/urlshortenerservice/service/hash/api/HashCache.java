package faang.school.urlshortenerservice.service.hash.api;

public interface HashCache {
    void initializing();

    String getHash();

    void ensureCacheIsFilled();
}