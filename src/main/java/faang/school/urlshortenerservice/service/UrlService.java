package faang.school.urlshortenerservice.service;

public interface UrlService {
    String findOriginalUrl(String hash);
}