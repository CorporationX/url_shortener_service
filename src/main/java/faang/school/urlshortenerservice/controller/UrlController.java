package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/url")
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<String> createShortenedUrl(@RequestBody @Validated UrlDto url) {
        return ResponseEntity
                .ok(urlService.createShortenedUrl(url));
    }

    @GetMapping("/{url}")
    public ResponseEntity<String> getOriginalUrl(@PathVariable String url) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(urlService.getOriginalUrl(url)));

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .body("Redirected");
    }
}
