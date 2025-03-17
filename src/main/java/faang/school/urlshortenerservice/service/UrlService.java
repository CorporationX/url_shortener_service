package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;

public interface UrlService {
    ShortUrlDto createShortUrl(UrlDto fullUrlDto);

    UrlDto getUrl(String hash);
}
