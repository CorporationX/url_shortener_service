package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/url")
public class UrlV1Controller {

    private final UrlService urlService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShortUrlDto generateShortUrl(@RequestBody @Valid UrlDto urlDto) {
        return urlService.generateShortUrl(urlDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.FOUND)
    public UrlDto getUrl(@RequestBody @Valid ShortUrlDto shortUrlDto) {
        return urlService.getUrlByShortUrl(shortUrlDto);
    }
}
