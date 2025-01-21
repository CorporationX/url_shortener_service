package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/url")
@RequiredArgsConstructor
@Validated
public class UrlController {

    private final UrlService urlService;

    @Value("api.url-body")
    private String urlBody;

    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody UrlDto urlDto) {
        String shortUrl = urlBody + urlService.getShortUrl(urlDto);
        return ResponseEntity.ok().header(shortUrl).build();
    }

    @GetMapping("/{hash}")
    public ResponseEntity<String> get(@RequestParam @Valid @Pattern(regexp = "^(http|https)://.*$", message = "This is not URL")
                                          String url) {
        String originalUrl = urlService.getOriginalUrl(url);
        return ResponseEntity.status(302).header("Location", originalUrl).build();
    }

}
