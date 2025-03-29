package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;

public interface UrlService {

    String getShortUrl(UrlDto urlDto);

    String redirectToRealUrl(String hash);
}
