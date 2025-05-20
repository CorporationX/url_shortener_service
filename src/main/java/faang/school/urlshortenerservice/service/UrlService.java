package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;

public interface UrlService {

    String getOriginalUrl(String hash);

    String createAndSaveShortUrl(UrlDto urlDto);
}
