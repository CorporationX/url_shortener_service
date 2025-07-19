package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.RedirectResponse;
import faang.school.urlshortenerservice.dto.ShortUrlRequest;
import faang.school.urlshortenerservice.dto.ShortUrlResponse;
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

@RestController
@RequestMapping("/api/v1/shorter")
@RequiredArgsConstructor
public class UrlController {

    private final UrlControllerFacade urlControllerFacade;

    @GetMapping("/{hash}")
    public ResponseEntity<RedirectResponse> getResource(@PathVariable String hash) {
        RedirectResponse redirectResponse = urlControllerFacade.getResource(hash);
        return ResponseEntity.status(HttpStatus.FOUND).body(redirectResponse);
    }

    @PostMapping
    public ResponseEntity<ShortUrlResponse> createShortUrl(@Valid @RequestBody ShortUrlRequest shortUrlRequest) {
        ShortUrlResponse shortUrlResponse = urlControllerFacade.createShortUrl(shortUrlRequest);
        return ResponseEntity.ok(shortUrlResponse);
    }
}
