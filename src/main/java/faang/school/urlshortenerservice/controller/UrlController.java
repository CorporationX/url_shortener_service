package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/url")
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/url")
    public ResponseEntity<String> shortenUrl(@RequestBody UrlDto urlDto) {
        if (!isValidUrl(urlDto.getLongUrl())) {
            return ResponseEntity.badRequest().body("Invalid URL");
        }
        String shortUrl = urlService.shortenUrl(urlDto.getLongUrl());
        return ResponseEntity.ok(shortUrl);
    }

    private boolean isValidUrl(String url) {
        // Реализация валидации URL
        return true; // Пример простого проверки
    }
}
