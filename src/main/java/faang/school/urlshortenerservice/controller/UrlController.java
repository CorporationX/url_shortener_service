package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public void processUrl(@Valid @RequestBody UrlDto urlDto) {
        urlService.processUrl(urlDto);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<String> redirect(@PathVariable String hash) {
        String originalUrl = urlService.getUrl(hash);
        return ResponseEntity.status(HttpStatus.FOUND)
                .body(originalUrl);
    }
}
