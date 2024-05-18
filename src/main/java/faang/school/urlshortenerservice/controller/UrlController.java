package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.url.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public UrlDto getShortUrl(@Valid UrlDto urlDto) {
        return urlService.getShortUrl(urlDto);
    }

    @GetMapping("/{hash}")
    public RedirectView getRedirectUrl(@PathVariable String hash) {
        return urlService.getRedirectUrl(hash);
    }
}
