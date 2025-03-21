package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.shortener.ShortenerProperties;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlUtilService {

    private final UrlService urlService;
    private final ShortenerProperties shortenerProperties;

    public UrlResponseDto shortenUrl(UrlRequestDto urlRequestDto) {
        log.info("Begin shorting url {}", urlRequestDto);

        UrlResponseDto urlResponseDto = urlService.getUrl(urlRequestDto.url());
        if (urlResponseDto.getShortUrl().isBlank()) {
            urlResponseDto = urlService.createCachedUrl(urlRequestDto.url());
        }

        return urlResponseDto;
    }

    /*public UrlResponseDto getFullUrl(UrlRequestDto urlRequestDto) {
        log.info("Try to get redirect url for short url: {}", urlRequestDto.shortUrl());
        String hash = urlRequestDto.shortUrl().substring(shortenerProperties.url().prefix().length());

        UrlResponseDto urlResponseDto = urlService.getUrlByHash(hash);
        if (urlResponseDto != null) {
            log.info("Found redirect url {}", urlResponseDto.url());
            return urlResponseDto;
        } else {
            throw new IllegalArgumentException("Unknown url " + urlRequestDto.shortUrl());
        }
    }*/

}
