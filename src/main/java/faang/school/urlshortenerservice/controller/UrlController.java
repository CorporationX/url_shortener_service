package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public ResponseEntity<String> getShortUrl(@RequestBody @Valid UrlDto urlDto) {
        String shortUrl = urlService.getShortUrl(urlDto);
        return ResponseEntity
                .ok()
                .body(shortUrl);
    }
}