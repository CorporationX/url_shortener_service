package faang.school.urlshortenerservice.controller;


import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
@Validated
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable String hash) {
        String longUrl = urlService.getLongUrlByHash(hash);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(longUrl))
                .build();
    }

    @PostMapping("/{urlDto}")
    public ResponseEntity<Void> redirectToShortUrl(@Valid @RequestBody UrlDto urlDto) {

    }

}
