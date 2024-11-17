package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.model.dto.UrlRequestDto;
import faang.school.urlshortenerservice.model.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/url")
public class UrlController {
    private final UrlRepository urlRepository;
    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<String> createShortUrl(@RequestBody @Valid UrlRequestDto urlRequestDto) {
        String shortUrl = urlService.createShortUrl(urlRequestDto.getOriginalUrl());
        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String hash) {
        Url url = urlRepository.findById(hash)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "URL not found"));

        URI uri = URI.create(url.getOriginalUrl());

        return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
    }
}