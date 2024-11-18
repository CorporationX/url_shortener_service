package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.dto.UrlDto;

public interface UrlService {
    String getShortUrl(UrlDto urlDto);

    String getOriginalUrl(String hash);
}
