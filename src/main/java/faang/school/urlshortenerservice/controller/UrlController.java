package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Tag(name = "URL Controller")
@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    @Operation(summary = "Create short URL")
    public RedirectView createShortUrl(@RequestBody @Valid UrlDto urlDto) {
        String shortUrl = urlService.createShortUrl(urlDto.getUrl());
        RedirectView redirectView = new RedirectView(shortUrl);
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }

    @GetMapping("/{hash}")
    @Operation(summary = "Get long URL")
    public RedirectView createLongUrl(@PathVariable String hash) {
        String longUrl = urlService.getLongUrl(hash);
        RedirectView redirectView = new RedirectView(longUrl);
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }
}
