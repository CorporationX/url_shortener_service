package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.URLRequestDto;
import faang.school.urlshortenerservice.exception.URLNotFoundException;
import faang.school.urlshortenerservice.service.URLService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class URLController {
    private final URLService urlService;

    @Value("${service.base-url}")
    private String baseUrl;

    @PostMapping("/shorten")
    public String shortenURL(@Valid @RequestBody URLRequestDto request) {
        String hash = urlService.createShortURL(request);
        return UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .path(hash)
                .build()
                .toUriString();
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToOriginal(@PathVariable String hash) {
        try {
            String originalUrl = urlService.getOriginalURL(hash);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(originalUrl))
                    .build();
        } catch (URLNotFoundException e) {
            log.warn("URL not found for hash: {}", hash);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error retrieving URL for hash: {}", hash, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
