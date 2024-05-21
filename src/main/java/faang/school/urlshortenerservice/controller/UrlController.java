package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlShortenerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/shortener")
public class UrlController {
    private final UrlShortenerService urlShortenerService;

    @PostMapping("/url")
    @CacheEvict(value = "url", key = "#url")
    public UrlDto convertToShortUrl(@RequestBody @Valid UrlDto urlDto) {
        return urlShortenerService.convertToShortUrl(urlDto);
    }
}
