package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/url")
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public UrlDto shortenUrl(@RequestBody UrlDto url) {
        return urlService.createShortLink(url);
    }

    @GetMapping("/hash")
    public RedirectView getUrls(@RequestParam String hash) {
        return new RedirectView(urlService.getUrl(hash));
    }
}
