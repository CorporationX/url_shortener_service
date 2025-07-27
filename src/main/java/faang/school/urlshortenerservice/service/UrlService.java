package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;

import java.util.List;

public interface UrlService {
    UrlResponseDto createShortUrl(UrlRequestDto url);

    UrlResponseDto getUrl(String hash);

    List<String> retrieveOldUrls(int daysCount);
}
