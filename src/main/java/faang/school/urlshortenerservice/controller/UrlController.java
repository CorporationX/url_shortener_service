package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlReadDto;
import faang.school.urlshortenerservice.dto.UrlShortenerDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public UrlReadDto shortenUrl(@RequestBody @Valid UrlShortenerDto urlShortenerDto) {
        return urlService.shortenUrl(urlShortenerDto.getUrl());
    }
}
