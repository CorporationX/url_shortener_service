package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.RequestUrl;
import faang.school.urlshortenerservice.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shortener")
@Tag(
        name = "Url Shortener",
        description = "Url Shortener API"
)
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/short")
    @Operation(summary = "Get short url")
    public String getHash(@RequestBody RequestUrl url) {
        return urlService.getHash(url.getUrl());
    }

    @GetMapping("/long")
    @Operation(summary = "Get long url")
    public ResponseEntity<String> getUrl(@RequestBody RequestUrl shortUrl) {
        String url = urlService.getLongUrl(shortUrl.getUrl());
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url)).build();
    }
}
