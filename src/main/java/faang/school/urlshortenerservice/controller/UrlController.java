package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlHashCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/url")
public class UrlController {

    private final UrlHashCacheService urlHashCacheService;
    @GetMapping("/{hash}")
    public ResponseEntity<String> getByHash(@RequestParam("hash") String hash) {
        String url = urlHashCacheService.getUrlByHash(hash);
        return ResponseEntity.status(HttpStatus.FOUND)
                .body(url);
    }
}
