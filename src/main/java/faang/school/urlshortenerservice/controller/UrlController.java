package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/url")
@RestController
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public String generateShortUrl(@RequestBody @Valid LongUrlDto longUrlDto) {
        return urlService.generateShortUrl(longUrlDto.getUrl());
    }
}
