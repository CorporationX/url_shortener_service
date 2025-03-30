package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequest;
import faang.school.urlshortenerservice.entity.FreeHash;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequiredArgsConstructor
@RequestMapping("/hash")
@RestController
public class UrlShortenerController {
    private final UrlService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/url")
    public String createShortUrl(@Valid @RequestBody UrlRequest request) {
        FreeHash generated = service.generateShortUrl(request.longUrl(), request.ttlSeconds());

        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/hash/{hash}")
                .buildAndExpand(generated.getHash())
                .toUriString();
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable String hash) {
        String longUrl = service.resolveLongUrl(hash);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", longUrl)
                .build();
    }
}
