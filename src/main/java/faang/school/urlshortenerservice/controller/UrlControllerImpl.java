package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/v1/url")
@RequiredArgsConstructor
public class UrlControllerImpl implements UrlController {

    private final UrlService urlService;

    @GetMapping
    public String getFullUrl(@RequestParam String shortUrl) {
        return urlService.getFullUrl(shortUrl);
    }

    @PostMapping
    public String createShortUrl(@RequestParam String fullUrl) {
        return urlService.createShortUrl(fullUrl);
    }
}
