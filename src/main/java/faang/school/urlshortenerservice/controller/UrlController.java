package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/url")
    public UrlDto shortenUrl(@Validated @RequestBody UrlDto urlDto) {
        return urlService.shortenUrl(urlDto);
    }

    @GetMapping("/{hash}")
    public UrlDto getUrl(@PathVariable String hash) {
        urlService.getNormalUrl(hash);
        return urlService.getNormalUrl(hash);
    }
}
