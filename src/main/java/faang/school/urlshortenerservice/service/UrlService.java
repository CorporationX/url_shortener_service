package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.CreateUrlDto;

public interface UrlService {
    String createShortUrl(CreateUrlDto dto);

    String getOriginalUrl(String hash);
}