package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public UrlResponseDto createShortUrl(@RequestBody @Valid UrlDto request) {
        String shortUrl = urlService.generateShortUrl(request.getUrl());
        log.debug("Shortened URL created: {}", shortUrl);
        return new UrlResponseDto(shortUrl);
    }

    @GetMapping("/hash/{hash}")
    public ResponseEntity<Void> redirect(@PathVariable String hash) {
        String longUrl = urlService.getLongUrl(hash);
        log.debug("Redirecting {} -> {}",hash, longUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, longUrl);
        return ResponseEntity.status(302).headers(headers).build();
    }
}
