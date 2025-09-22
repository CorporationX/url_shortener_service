package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.RedirectResponse;
import faang.school.urlshortenerservice.dto.ShortUrlRequest;
import faang.school.urlshortenerservice.dto.ShortUrlResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UrlController {

    private final UrlControllerFacade urlControllerFacade;

    @GetMapping("/{hash}")
    public ResponseEntity<RedirectResponse> getActualUrl(@PathVariable String hash) {
        RedirectResponse redirectResponse = urlControllerFacade.getActualUrl(hash);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(redirectResponse.url())
                .build();
    }

    @PostMapping
    public ResponseEntity<ShortUrlResponse> createShortUrl(@Valid @RequestBody ShortUrlRequest shortUrlRequest) {
        ShortUrlResponse shortUrlResponse = urlControllerFacade.createShortUrl(shortUrlRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(shortUrlResponse);
    }
}
