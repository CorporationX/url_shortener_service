package faang.school.urlshortenerservice.service;

public interface ShortUrlCacheService {
    String get(String code);

    void set(String code, String url);
}
