package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlResponseDto;

public interface UrlService {

    UrlResponseDto getOrCreateUrl(String urlAddress);

    UrlResponseDto getUrlByHash(String hash);
}
