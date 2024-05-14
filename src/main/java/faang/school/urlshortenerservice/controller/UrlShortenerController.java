package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlShortenerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/url-shortener")
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    @PostMapping("/url")
    @CacheEvict(value = "url", key = "#url")
    public UrlDto shortenUrl(@RequestBody @Valid UrlDto url) {
        return urlShortenerService.shortenUrl(url);
    }
    //should I return here Status 200 or 3xx (redirection)
}