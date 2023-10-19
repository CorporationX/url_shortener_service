package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.OriginalUrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/url")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/shorten")
    @ResponseStatus(HttpStatus.CREATED)
    public ShortUrlDto shorten(@RequestBody @Valid OriginalUrlDto urlDto) {
        return urlService.shorten(urlDto);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> get(@PathVariable String hash) {
        OriginalUrlDto originalUrl = urlService.getUrlByHash(hash);
        return ResponseEntity.status(302)
                .header("Location", originalUrl.getUrl())
                .build();
    }
}
