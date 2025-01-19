package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
@Slf4j
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public String getShortUrl(@Valid @RequestBody UrlDto urlDto) {
        log.info("Get short url: {}", urlDto.getOriginalUrl());
        return urlService.getShortUrl(urlDto);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String hash) {
        log.info("Received a request to redirect from url: {}", hash);

        String originalUrl = urlService.redirectToRealUrl(hash);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.LOCATION, originalUrl);

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
