package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.URLDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shortener")
public class URLController {
    private final UrlService urlService;

    @PostMapping("/url")
    public ResponseEntity<String> createShortLink(@Valid @RequestBody URLDto urlDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(urlService.createShortLink(urlDto));
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> getUrlByHash(@PathVariable String hash) {
        String url = urlService.getUrlByHash(hash);
        if (url == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}