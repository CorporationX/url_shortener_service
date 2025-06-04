package faang.school.urlshortenerservice.service;

public interface UrlService {

    String shortenUrl(String longUrl);

    String getOriginalUrl(String hash);
}
