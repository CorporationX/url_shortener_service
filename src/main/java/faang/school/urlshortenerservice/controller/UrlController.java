package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/url-shortener")
@RequiredArgsConstructor
@Validated
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToLongUrl(
            @PathVariable @Length(min = 6, max = 6, message = "Hash must be exactly 6 characters long")
            String hash) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(urlService.getLongUrl(hash)))
                .build();
    }

    @PostMapping()
    public String getShortUrl(
            @Valid @RequestBody UrlDto urlDto) {
        return urlService.getShortUrl(urlDto.getUrl());
    }
}
