package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public String getShortUrl(@Valid @RequestBody UrlDto urlDto) {
        log.info("Received a request to shorten URL: {}", urlDto.getUrl());
        return urlService.getShortUrl(urlDto);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String hash, HttpServletRequest request) {
        String currentUrl = request.getRequestURL().toString();
        log.info("Received a request to redirect from url: {}", currentUrl);

        String originalUrl = urlService.redirectToRealUrl(hash);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
