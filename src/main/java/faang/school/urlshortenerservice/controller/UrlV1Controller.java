package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("api/v1/urls")
@RequiredArgsConstructor
public class UrlV1Controller {

    private final UrlService urlService;

    @PostMapping("/shorten")
    @ResponseStatus(HttpStatus.CREATED)
    public UrlDto shortenUrl(@RequestBody @Valid UrlDto urlDto) {
        return urlService.shortenUrl(urlDto);
    }

    @GetMapping("/{hash}")
    public RedirectView getOriginalUrl(@PathVariable String hash) {
        String originalUrl = urlService.getOriginalUrl(hash);
        return new RedirectView(originalUrl);
    }
}
