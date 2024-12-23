package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;

public interface UrlService {

    String getUrl(String hash);

    String getShortUrl(UrlDto urlDto);

}
