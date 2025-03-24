package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequest;
import faang.school.urlshortenerservice.entity.FreeHash;
import faang.school.urlshortenerservice.service.UrlShortenerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final UrlShortenerService service;

    @Value("${shortener.max-url-expired-minutes}")
    private int maxTtlMinutes;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/get")
    public String createShortUrl(@Valid @RequestBody UrlRequest request) {
        int ttlMinutes = checkTtlThrow(request);

        FreeHash generated = service.generateShortUrl(request.longUrl(), ttlMinutes);

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

    private int checkTtlThrow(UrlRequest request) {
        if (request.ttlMinutes() > maxTtlMinutes) {
            throw new IllegalArgumentException();
        }
        return request.ttlMinutes();
    }
}
