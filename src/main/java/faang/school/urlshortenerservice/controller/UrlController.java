package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService service;

    @PostMapping("/url/")
        public String getShortLink(@Valid @RequestBody @NonNull UrlRequestDto urlRequestDto) {
        return service.getShortUrlLink(urlRequestDto.url());
    }

    @GetMapping("/url/{hash}")
        public ResponseEntity<Void> redirect(@PathVariable String hash) {
        String originalUrl = service.getOriginalUrl(hash);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

}
