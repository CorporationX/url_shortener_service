package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.dto.url.UrlDto;

public interface UrlService {

    String shortenUrl(UrlDto urlDto);

    String getOriginalUrl(String hash);
}
