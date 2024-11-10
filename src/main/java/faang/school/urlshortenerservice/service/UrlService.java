package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.dto.UrlDto;

public interface UrlService {
    UrlDto shortenUrl(UrlDto urlDto);
    UrlDto getNormalUrl(String hash);
}
