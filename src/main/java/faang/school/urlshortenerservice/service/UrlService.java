package faang.school.urlshortenerservice.service;

public interface UrlService {
    String shortAndReturn(String longUrl);
    String findOriginal(String hash);
}
