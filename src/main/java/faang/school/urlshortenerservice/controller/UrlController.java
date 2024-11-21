package faang.school.urlshortenerservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import faang.school.urlshortenerservice.dto.Link;
import faang.school.urlshortenerservice.service.UrlService;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/url")
    public String comingUrl(@Valid @RequestBody Link link) {
        return urlService.createShortUrl(link);
    }

    @GetMapping("/{hash}")
    public RedirectView convertedUrl(@PathVariable String hash) {
        return new RedirectView(urlService.getShortUrl(hash));
    }
}
