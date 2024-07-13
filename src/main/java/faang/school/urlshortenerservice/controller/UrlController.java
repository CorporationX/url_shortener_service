package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
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
    public UrlDto createShortUrl(@Valid @RequestBody UrlDto urlDto) {
        return urlService.createShortUrl(urlDto);
    }

    @GetMapping("/{hash}")
    public RedirectView getRedirectView(@PathVariable("hash") String hash) {
        RedirectView redirectView = urlService.getRedirectView(hash);
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }
}
