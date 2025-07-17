package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlResponseDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/url")
public class UrlController {

    private final UrlService urlService;

    @Value("${server.base-url}")
    private String baseUrl;

    @PostMapping
    public ShortUrlResponseDto createShortUrl(@Valid @RequestBody UrlDto urlDto) {
        String hash = urlService.createShortUrl(urlDto.getUrl());
        return ShortUrlResponseDto.builder()
                .shortUrl(baseUrl + "/" + hash)
                .build();
    }
}