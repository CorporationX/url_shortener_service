package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.ShortUrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;

import java.time.LocalDateTime;
import java.util.List;

public interface UrlService {

    List<Url> pollOldUrls(LocalDateTime minus);

    ShortUrlResponseDto createHashedUrl(String url);

    String getRealUrlByHash(String hash);
}
