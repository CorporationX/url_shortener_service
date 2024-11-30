package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/url")
@RequiredArgsConstructor
@Validated
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public String generateShortUrl(@Validated @RequestBody UrlDto url) {
        return urlService.generateShortUrl(url);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> getUrl(@PathVariable @NotBlank String shortUrl) {
        String url = urlService.getUrl(shortUrl);
        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(url))
            .build();
    }

}
