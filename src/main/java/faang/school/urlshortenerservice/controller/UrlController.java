package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.url.RequestUrlDto;
import faang.school.urlshortenerservice.dto.url.ResponseUrlDto;
import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public UrlDto createShortUrl(@RequestBody @Valid RequestUrlDto requestUrlDto) {
        return urlService.createShortUrl(requestUrlDto);
    }

    @GetMapping("/{hash}")
    public RedirectView getFullUrl(@PathVariable String hash) {
        return new RedirectView(urlService.getFullUrl(hash).getUrl());
    }
}
