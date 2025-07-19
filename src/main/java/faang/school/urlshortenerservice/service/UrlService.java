package faang.school.urlshortenerservice.service;

public interface UrlService {
    String shortAndReturn(String longUrl);
    String findOriginalUrl(String hash);
}
