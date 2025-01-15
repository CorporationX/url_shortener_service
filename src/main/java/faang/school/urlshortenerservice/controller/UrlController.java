package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/urls")
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<Void> createUrl(@RequestBody UrlDto urlDto) {
        urlService.saveNewHash(urlDto);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> getUrl(@PathVariable String hash) {
        String url = urlService.findUrl(hash);
        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, url)
                .build();
    }
}
