package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/url")
public class UrlController {
    private final UrlService urlService;

    @PostMapping()
    public ResponseEntity<String> create(@Valid @RequestBody UrlDto urlDto) {
        return ResponseEntity.status(HttpStatus.OK).body(urlService.create(urlDto));
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> getUrlByHash(@PathVariable String hash){
        String url = urlService.getUrlByHash(hash);
        if (url == null){
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}