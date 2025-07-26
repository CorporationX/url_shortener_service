package faang.school.urlshortenerservice.controller;

public interface UrlController {

    String getFullUrl(String shortUrl);

    String createShortUrl(String fullUrl);
}
