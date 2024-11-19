package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public String shortenUrl(@RequestParam(name = "url") String url) {
        return urlService.shortenUrl(url);
    }

    @GetMapping("/Hash/{hash}")
    public ResponseEntity<Void> getUrl(@PathVariable String hash) {
        String url = urlService.getUrl(hash);
        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, url)
                .build();
    }
}
