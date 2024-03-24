package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;

public interface UrlService {

    String getShortenUrl(UrlDto url);

    String getOriginalUrl(String hash);

}
