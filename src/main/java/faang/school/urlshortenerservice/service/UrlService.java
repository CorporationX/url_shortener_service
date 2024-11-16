package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;

public interface UrlService {

    UrlDto convertShortUrl(UrlDto urlDto);

    Url getUrl(String hash);

}
