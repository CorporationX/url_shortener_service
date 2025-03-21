package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlShortenerRequest;
import faang.school.urlshortenerservice.dto.UrlShortenerResponse;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/url")
@Slf4j
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<?> createShortLink(@RequestBody UrlShortenerRequest request) {
        UrlShortenerResponse response = urlService.create(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<?> redirect(@PathVariable String hash) {
        UrlShortenerResponse response = urlService.getEndPoint(hash);
        log.info("redirecting..");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", response.endPoint());
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}