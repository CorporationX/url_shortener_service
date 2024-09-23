package faang.school.urlshortenerservice.service;

public interface HashCache {

    String getHash();
    void refill();
    void refillAsync();
}
