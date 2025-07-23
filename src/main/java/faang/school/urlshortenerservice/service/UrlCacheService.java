package faang.school.urlshortenerservice.service;

public interface UrlCacheService {

    void saveNewPair(String hash, String longUrl);

    String getByHash(String hash);

    void deletePairByHash(String hash);
}
