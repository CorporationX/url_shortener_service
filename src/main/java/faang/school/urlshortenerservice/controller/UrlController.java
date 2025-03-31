package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequest;
import faang.school.urlshortenerservice.dto.UrlResponse;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/url")
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public UrlResponse getShortUrl(@Valid @RequestBody UrlRequest url) {
        return urlService.getShortUrl(url);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> getOriginalUrl(@PathVariable String hash) {
        String url = urlService.getOriginalUrl(hash);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header("Location", url)
                .build();
    }
}
