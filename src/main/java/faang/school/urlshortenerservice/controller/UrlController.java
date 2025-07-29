package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.MalformedUrlException;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class UrlController {
    private final UrlService urlService;
    @Value("${app.base-url}")
    private String baseUrl;

    @PostMapping("/url")
    public ResponseEntity<String> shortenUrl(@RequestBody @Valid UrlDto urlDto) {
        validateUrl(urlDto.getUrl());
        String shortUlr = baseUrl + "/" + urlService.processLongUrl(urlDto);
        return new ResponseEntity<>(shortUlr, HttpStatus.CREATED);
    }

    @GetMapping("/{hash}")
    public String getLongUrl(@PathVariable @Size(min = 6, max = 6, message = "The hash must be 6 characters")
                             String hash) {
        String originalUrl = urlService.getOriginalUrl(hash);
        if (originalUrl == null || originalUrl.isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Original URL not found");
        }
        if (!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")) {
            originalUrl = "https://" + originalUrl;
        }
        String url = UriComponentsBuilder.fromHttpUrl(originalUrl)
                .build()
                .toUriString();
        return "redirect:" + url;
    }

    private void validateUrl(String rawUrl) {
        URI uri;
        try {
            uri = new URI(rawUrl);
        } catch (URISyntaxException e) {
            throw new MalformedUrlException(String.format("URL '%s' is invalid", rawUrl));
        }

        String scheme = uri.getScheme();
        if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
            throw new MalformedUrlException("URL must start with http:// or https://. Provided URL: " + rawUrl);
        }

        String host = uri.getHost();
        if (host == null || host.isBlank()) {
            throw new MalformedUrlException("URL must have a valid host");
        }
        if (host.equals("localhost") || host.equals("127.0.0.1") || host.endsWith(".internal")) {
            throw new MalformedUrlException("URL cannot point to internal network. Provided URL: " + rawUrl);
        }
    }
}