package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.CreateShortUrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
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

import java.net.URI;

@RestController
@Slf4j
@RequestMapping("${request_mapping.url_controller:/api/v1/urls}")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<ShortUrlDto> createShortUrl(@RequestBody @Valid CreateShortUrlDto createShortUrlDto) {
        ShortUrlDto result = urlService.createShortUrl(createShortUrlDto);
        return ResponseEntity
                .created(URI.create(result.shortUrl()))
                .body(result);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> passOriginalUrl(@PathVariable String hash) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(urlService.getOriginalUrl(hash)))
                .build();
    }
}
