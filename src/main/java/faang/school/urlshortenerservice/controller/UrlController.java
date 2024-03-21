package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.RequestUrl;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/url")
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public String getHash(@RequestBody RequestUrl url) {
        return urlService.getHash(url.getUrl());
    }

    @GetMapping("/url/{hash}")
    public ResponseEntity<String> getUrl(@PathVariable String hash) {
        String url = urlService.getLongUrl(hash);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(java.net.URI.create(url)).build();
    }
}
