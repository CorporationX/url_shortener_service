package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.generator.RedisHashCache;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/url")
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public String createShortUrl(String longUrl) {
        log.debug("Creating a new URL - Started");
        validateUrl(longUrl);

        return new String();
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable String hash) {}

    private void validateUrl(String longUrl) {
        if (longUrl == null || longUrl.isEmpty()) {
            throw new DataValidationException("URL cannot be null or empty");
        }
        boolean isValid = false;
        String[] urlStart = new String[]{"http", "https"};
        for (String start : urlStart) {
            if (longUrl.startsWith(start + "://")) {
                isValid = true;
                break;
            }
        }
        if (!isValid) {
            throw new DataValidationException("Invalid URL format");
        }
    }
}
