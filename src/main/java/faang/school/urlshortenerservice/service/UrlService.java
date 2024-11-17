package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;

public interface UrlService {

    UrlDto toShortUrl(UrlDto urlDto);

    Url getUrl(String hash);

}
