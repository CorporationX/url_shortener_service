package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;

import java.util.List;

public interface UrlService {
    UrlDto createShortUrl(UrlDto url);

    UrlDto getUrl(String hash);

    List<String> retrieveOldUrls(int daysCount);
}
