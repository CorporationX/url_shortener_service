package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.response.FullUrlResponseDto;

public interface UrlService {

    String getFullUrl(String shortUrl);

    FullUrlResponseDto createShortUrl(String fullUrl);
}
