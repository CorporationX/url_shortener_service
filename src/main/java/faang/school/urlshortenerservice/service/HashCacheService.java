package faang.school.urlshortenerservice.service;

public interface HashCacheService {
    String getHash();
    void returnHash(String hash);
    int size();
}

