package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.RequestUrlDto;
import faang.school.urlshortenerservice.dto.ResponseUrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirect(@PathVariable String hash) {
        String longUrl = urlService.getOriginalUrl(hash);
        return ResponseEntity
                .status(302)
                .location(URI.create(longUrl))
                .build();
    }

    @PostMapping("/url")
    public ResponseEntity<ResponseUrlDto> create(
            @Valid @RequestBody RequestUrlDto dto
            ) {
        String shortUrl = urlService.createShortUrl(dto.getUrl());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseUrlDto(shortUrl));
    }
}
