package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;

public interface UrlService {

    String shortenUrl(UrlDto url);

    String getOriginalUrl(String hash);

}
