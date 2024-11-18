package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlDto;

public interface UrlService {

    ShortUrlDto shortenUrl(UrlDto urlDto);

    String getOriginalUrl(String hash);
}
