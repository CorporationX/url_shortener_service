package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.model.dto.url.UrlDto;

public interface UrlService {

    String createShortUrl(UrlDto dto);

    String getOriginalUrl(UrlDto urlDto);
}
