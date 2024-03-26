package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public String getShortUrl(@RequestBody @Valid UrlDto urlDto) {
        return urlService.getShortUrl(urlDto.getUrl());
    }

    @GetMapping("/{hash}")
    public RedirectView getOriginalUrl(@PathVariable String hash) {
        RedirectView redirectView = new RedirectView(urlService.getOriginalUrl(hash));
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }
}
