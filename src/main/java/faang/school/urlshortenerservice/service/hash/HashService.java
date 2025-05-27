package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import org.springframework.http.ResponseEntity;

public interface HashService {
    public ResponseEntity<String> redirectToOriginalUrl(String hash);

    public UrlResponseDto createShortUrl(UrlRequestDto url);
}
