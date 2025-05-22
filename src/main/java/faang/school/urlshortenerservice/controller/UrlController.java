package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/shortener")
public class UrlController {

    private final UrlService urlService;

    @Value("${urlController.baseUrl}")
    private String baseUrl;

    @PostMapping()
    public String shortenUrl(@RequestBody UrlDto urlDto) {
        log.info("Received request to shorten URL: {}", urlDto.url());

        try {
            String shortUrl = urlService.shortenUrl(urlDto);
            String fullShortUrl = baseUrl + shortUrl;

            log.info("Successfully shortened URL. Original: {}, Short: {}",
                    urlDto.url(), fullShortUrl);

            return fullShortUrl;
        } catch (Exception e) {
            log.error("Failed to shorten URL: {}. Error: {}", urlDto.url(), e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String hash) {
        log.info("Received redirect request for hash: {}", hash);

        try {
            String originalUrl = urlService.getOriginalUrl(hash);

            if (originalUrl == null || originalUrl.isBlank()) {
                log.warn("No original URL found for hash: {}", hash);
                return ResponseEntity.notFound().build();
            }

            log.info("Redirecting hash {} to URL: {}", hash, originalUrl);

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(originalUrl))
                    .build();
        } catch (Exception e) {
            log.error("Error processing redirect for hash: {}. Error: {}", hash, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
