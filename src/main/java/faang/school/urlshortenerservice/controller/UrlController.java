package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.CreateUrlDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;

@RequiredArgsConstructor
@RestController
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public ResponseEntity<UrlDto> createShortUrl(@RequestBody @Valid CreateUrlDto dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new UrlDto(URI.create(urlService.createShortUrl(dto))));
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> get(@PathVariable("hash") String hash) {
        String url = urlService.getOriginalUrl(hash);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }
}