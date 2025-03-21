package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;

public interface UrlService {

    void cleanOutdatedUrls();

    String getUrl(String hash);

    UrlDto shortenUrl (UrlDto urlDto);
}
