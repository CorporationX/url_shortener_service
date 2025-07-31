package faang.school.urlshortenerservice.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.util.UriUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public UrlDto createUrl(@RequestBody UrlDto urlDto) {
        URI uri = UriUtils.converToUri(urlDto.getUrl());
        UrlDto generatedUrl = urlService.generateUrl(uri);

        return generatedUrl;
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectUrl(@PathVariable String hash) {
        UrlDto urlDto = urlService.getUrlByHash(hash);

        return ResponseEntity
            .status(HttpStatus.FOUND)
            .location(URI.create(urlDto.getUrl()))
            .build();
    }
}
