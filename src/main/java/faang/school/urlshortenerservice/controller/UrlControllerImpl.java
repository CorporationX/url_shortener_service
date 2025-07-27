package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.request.FullUrlRequestDto;
import faang.school.urlshortenerservice.dto.request.ShortUrlRequestDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class UrlControllerImpl {

    private final UrlService urlService;

    @GetMapping
    public String redirectToOriginalUrl(@RequestBody FullUrlRequestDto dto) {
        return urlService.getFullUrl(dto.getHash());
    }

    @PostMapping("/api/v1/url")
    public String createShortUrl(@RequestBody ShortUrlRequestDto dto) {
        return urlService.createShortUrl(dto.getFullUrl());
    }
}
