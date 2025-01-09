package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.url.UrlService;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated            // чтобы @URL и @NotEmpty отрабатывали
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/url")
    public ResponseEntity<String> createShortUrl(
            @RequestParam(name = "url") @NotEmpty @URL String originalUrl
    ) {
        String shortUrl = urlService.createHashUrl(originalUrl);
        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirect(@PathVariable String hash) {
        String originalUrl = urlService.getPrimalUri(hash);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, originalUrl)
                .build();
    }
}