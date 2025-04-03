package faang.school.urlshortenerservice.service.hash.api;

public interface HashCache {
    String getHash();

    void fillCache();
}