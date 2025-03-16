package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlResponseDto;

public interface UrlService {

    UrlResponseDto createCachedUrl(String urlAddress);

    UrlResponseDto getUrl(String urlAddress);

    UrlResponseDto getUrlByHash(String hash);



}
