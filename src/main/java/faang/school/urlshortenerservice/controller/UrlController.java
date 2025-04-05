package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/url")
@RestController
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<String> generateShortUrl(
            @Parameter(description = "Original URL to be shortened")
            @RequestBody @Valid String url) {

        String result = urlService.generateShortUrl(url);

        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Void> getUrlByHash(
            @Parameter(description = "Hash of Original URL in service")
            @PathVariable String shortUrl) {
        if (!StringUtils.hasText(shortUrl)) {
            return ResponseEntity.notFound().build();
        }

        String originalUrl = urlService.getUrl(shortUrl);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
