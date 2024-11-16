package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.model.dto.UrlRequestDto;
import faang.school.urlshortenerservice.service.impl.UrlServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/url-shortener")
public class UrlController {
    private final UrlServiceImpl urlService;

    @GetMapping("/{hash}")
    public RedirectView redirectUrl(@PathVariable String hash) {
        String originalUrl = urlService.getUrlByHash(hash);
        RedirectView redirectView = new RedirectView(originalUrl);
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }

    @PostMapping
    public String createShortUrl(@Valid @RequestBody UrlRequestDto urlRequestDto) {
        return urlService.createShortUrl(urlRequestDto.getOriginalUrl());
    }
}
