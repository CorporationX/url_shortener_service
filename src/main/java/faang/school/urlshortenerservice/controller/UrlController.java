package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDTO;
import faang.school.urlshortenerservice.exception.url.InvalidUrlException;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
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

    @PostMapping
    public String shortenUrl(@RequestBody UrlDTO requestDTO) {
        String longUrl = requestDTO.getUrl();
        log.info("Received request to shorten URL: {}", longUrl);
        if (!isValidUrl(longUrl)) {
            throw new InvalidUrlException("Invalid URL");
        }
        return urlService.shortenUrl(longUrl);
    }

    @GetMapping("/{hash}")
    public void redirectToOriginalURL(@PathVariable String hash, HttpServletResponse response) {
        String originalURL = urlService.getOriginalURL(hash);
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