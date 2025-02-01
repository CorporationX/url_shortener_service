package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlCreateDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import jakarta.validation.Valid;

public interface UrlService {

  UrlResponseDto createShortUrl(UrlCreateDto dto);

  String getOriginalUrl(@Valid String hash);
}
