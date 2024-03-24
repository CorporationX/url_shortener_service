package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @Value("${url-shortener-service.domain}")
    private String shortUrlDomain;

    @PostMapping
    public String createUrl(@RequestBody @Valid UrlDto urlDto) {
        return shortUrlDomain + urlService.saveUrl(urlDto);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Object> getUrl(@PathVariable String hash) throws URISyntaxException {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(urlService.getOriginalUrl(hash).getUrl()))
                .build();
    }
}