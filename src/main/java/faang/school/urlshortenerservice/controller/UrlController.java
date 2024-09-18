package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDtoRequest;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String getShortenUrl(@RequestBody @Valid UrlDtoRequest request) {
        return urlService.getShortUrl(request);
    }

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public RedirectView getOriginalUrl(@PathVariable String hash) {
        RedirectView redirectView = new RedirectView();
        String redirectUrl = urlService.getUrlFromHash(hash);
        redirectView.setUrl(redirectUrl);

        return redirectView;
    }
}