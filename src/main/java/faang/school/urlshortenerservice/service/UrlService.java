package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;

public interface UrlService {

    String getUrl(String hash);

    String getUrlBy(String hash);

    String getUrlFromDatabaseBy(String hash);

    String generateHashForUrl(UrlDto urlDto);

    void clearOutdatedUrls();
}
