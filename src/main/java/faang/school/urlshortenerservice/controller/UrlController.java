package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequest;
import faang.school.urlshortenerservice.dto.UrlResponse;
import faang.school.urlshortenerservice.facade.UrlFacade;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/shortener")
public class UrlController {

    private final UrlFacade urlFacade;

    @PostMapping
    public ResponseEntity<UrlResponse> createShortUrl(@Valid @RequestBody UrlRequest urlRequest) {
        UrlResponse urlResponse = urlFacade.createShortUrl(urlRequest);
        return ResponseEntity.created(urlResponse.getUrl()).body(urlResponse);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<URI> getShortUrl(@PathVariable String hash) {
        UrlResponse urlResponse = urlFacade.getShortUrl(hash);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(urlResponse.getUrl().toString()))
                .body(urlResponse.getUrl());
    }


}
