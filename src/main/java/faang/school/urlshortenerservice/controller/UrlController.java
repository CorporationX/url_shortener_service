package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
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
@RequestMapping("/urls")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @GetMapping("/{hash}")
    public ResponseEntity<Void> getUrl(@PathVariable String hash) {
        String originalUrl = urlService.getOriginalUrl(hash);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @PostMapping
    public UrlDto shortenUrl(@RequestBody @Valid UrlDto urlDto) {
        return urlService.shortenUrl(urlDto);
    }
}
