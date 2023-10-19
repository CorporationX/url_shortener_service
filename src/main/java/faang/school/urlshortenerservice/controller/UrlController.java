package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDTO;
import faang.school.urlshortenerservice.exception.url.InvalidUrlException;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/url")
public class UrlController {
    private final UrlService urlService;
    @Value("${url.shortener-service.address}")
    private String serverAddress;

    @PostMapping
    public ResponseEntity<String> shortenUrl(@RequestBody UrlDTO urlDTO) {
        String shortUrl = urlService.shortenUrl(urlDTO.getUrl());
        log.info("Received request to shorten URL: {}", urlDTO.getUrl());

        if (!isValidUrl(urlDTO.getUrl())) {
            throw new InvalidUrlException("Invalid URL");
        }

        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("/{hash}")
    public void redirectToOriginalURL(@PathVariable String hash, HttpServletResponse response) {
        String originalURL = urlService.getOriginalURL(serverAddress + hash);
        if (originalURL != null) {
            response.setHeader("Location", originalURL);
            response.setStatus(HttpServletResponse.SC_FOUND);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private boolean isValidUrl(String url) {
        UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https", "ftp"});

        return urlValidator.isValid(url);
    }
}