package faang.school.urlshortenerservice.andreev.service.hash;

import faang.school.urlshortenerservice.andreev.dto.UrlRequestDto;
import faang.school.urlshortenerservice.andreev.dto.UrlResponseDto;
import org.springframework.http.ResponseEntity;

public interface HashService {
    public ResponseEntity<String> redirectToOriginalUrl(String hash);

    public UrlResponseDto createShortUrl(UrlRequestDto url);
}
