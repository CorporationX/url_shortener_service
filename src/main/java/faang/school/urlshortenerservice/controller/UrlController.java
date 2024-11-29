package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    @ResponseStatus(HttpStatus.CREATED)
    public String shorten(@RequestBody @Valid UrlDto url) {
        String longUrl = url.getUrl();
        return urlService.shorten(longUrl);
    }

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<?> getUrls(@PathVariable String hash) {
        String longUrl = urlService.getUrl(hash);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header("Location", longUrl)
                .build();
    }
}
