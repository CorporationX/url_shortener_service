package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/v1/shortener")
@RequiredArgsConstructor
public class UrlController {

    private final UrlShortenerService urlShortenerService;

    @GetMapping("/{hash}")
    public RedirectView getUrl(@PathVariable String hash) {
        String longUrl = urlShortenerService.getUrl(hash);

        return new RedirectView(longUrl);
    }
}
