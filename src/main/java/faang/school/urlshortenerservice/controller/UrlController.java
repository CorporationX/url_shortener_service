package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
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
@RequestMapping("/api/v1/url")
@RequiredArgsConstructor
public class UrlController {
    private final UrlFacade urlFacade;

    @GetMapping("/{hash}")
    public ResponseEntity<String> getUrlByHash(@PathVariable String hash) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(urlFacade.getUrlByHash(hash)))
                .build();
    }

    @PostMapping
    public ResponseEntity<String> generateHash(@RequestBody @Valid UrlRequestDto urlRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(urlFacade.generateHash(urlRequestDto));
    }
}
