package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping()
    public URL convertToShortUrl(@RequestBody URL url) {
        return urlService.convertToShortUrl(url);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> getFullUrl(@PathVariable String hash) {
        String url = urlService.getFullUrl(hash);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", url)
                .build();
    }
}
