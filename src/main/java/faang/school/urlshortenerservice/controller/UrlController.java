package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.url.UrlService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/v1/urls")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable @NotNull String hash) {
        String longUrl = urlService.getLongUrl(hash);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(longUrl))
                .build();
    }

    @PostMapping
    public UrlDto createShortUrl(@RequestBody @Valid UrlDto urlDto) {
        return urlService.createShortUrl(urlDto);
    }
}
