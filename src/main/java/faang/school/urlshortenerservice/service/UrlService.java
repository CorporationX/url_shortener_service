package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.dto.UrlRequestDto;

public interface UrlService {

    String createShortUrl(UrlRequestDto urlRequest);
    String getLongUrl(String hash);

}