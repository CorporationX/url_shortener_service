package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;

public interface UrlService {

    UrlResponseDto createShortUrl(UrlRequestDto url);

    String getOriginalUrl(String hash);
}
