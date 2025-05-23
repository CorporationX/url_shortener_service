package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.ShortUrlRequestDto;
import org.springframework.web.bind.annotation.RequestBody;

public interface UrlService {

    String getShortUrl(@RequestBody ShortUrlRequestDto requestDto);
}
