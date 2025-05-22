package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class UrlController {

    private final UrlService urlService;

    @PostMapping("url")
    public ResponseEntity<String> createShortUrl(@RequestBody @Valid UrlRequestDto requestDto) {
        String hash = urlService.createShortUrl(requestDto.getUrl());
        return ResponseEntity.ok(hash);
    }

    @GetMapping("{hash}")
    public ResponseEntity<Void> redirect(@PathVariable String hash) {
        String originalUrl = urlService.getOriginalUrl(hash);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", originalUrl);
        return ResponseEntity.status(302).headers(headers).build();
    }
}
