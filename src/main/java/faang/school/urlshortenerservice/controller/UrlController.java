package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping("url")
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public String createShortLink(@RequestBody @Valid UrlDto url) {
        return urlService.createShortLink(url);
    }

    @GetMapping("/{shortUrl}")
    public RedirectView redirectToOriginalUrl(@PathVariable String shortUrl) {
        String originalUrl = urlService.getOriginUrl(shortUrl);
        return new RedirectView(originalUrl);
    }
}
