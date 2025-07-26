package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/url")
@RequiredArgsConstructor
@Slf4j
public class UrlControllerImpl implements UrlController {

    private final UrlService urlService;

    @GetMapping
    public String getFullUrl(@RequestParam String shortUrl) {
        return urlService.getFullUrl(shortUrl);
    }

    @PostMapping
    public String createShortUrl(@RequestParam String fullUrl) {
        log.info("Received POST request with fullUrl: {}", fullUrl);
        return urlService.createShortUrl(fullUrl);
    }
}
