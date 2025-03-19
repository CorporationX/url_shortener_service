package faang.school.urlshortenerservice.service;

public interface UrlService {

    void cleanOutdatedUrls();

    String getUrl(String hash);
}
