package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.ShortUrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface UrlService {

    List<Url> pollOldUrls(LocalDateTime minus);

    ShortUrlResponseDto createShortUrl(String url, HttpServletRequest request);

    String getRealUrlByHash(String hash);
}
