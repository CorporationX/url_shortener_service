package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.service.url.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Valid
@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @GetMapping("/{hash}")
    public RedirectView getRedirectView(@PathVariable("hash") String hash) {
        return urlService.getRedirectView(hash);
    }

    @PostMapping("/url")
    public HashDto generateShortUrl(@RequestBody UrlDto dto) {
        return urlService.generateShortUrl(dto);
    }
}