package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;

public interface UrlService {

    String redirectByHash(String hash);

    String getUrlBy(String hash);

    String getUrlFromDatabaseBy(String hash);

    String shortenUrl(UrlDto urlDto);

    void clearOutdatedUrls();
}
