package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/url-shortener")
@Slf4j
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/url")
    public ResponseEntity<HashDto> createShortUrl(@RequestBody @Valid UrlDto urlDto) {
        return ResponseEntity.ok(urlService.createShortUrl(urlDto));
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirect(@PathVariable String hash) {
            String originalUrl = urlService.getOriginalUrl(hash);
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .header("Location", originalUrl)
                    .build();
    }
}
