package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.UrlShortDto;

public interface UrlService {

    UrlShortDto createShortUrl(UrlDto dto);

    UrlDto getLongUrl(String hash);
}
