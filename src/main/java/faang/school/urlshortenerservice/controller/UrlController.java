package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/url")
public class UrlController {
    private final UrlService urlService;

    @Operation(summary = "Create short url", description = "Creating short url and save in db")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public String createShortUrl(@RequestBody @Validated(UrlDto.Create.class) UrlDto urlDto) {
        return urlService.createShortUrl(urlDto);
    }

    @Operation(summary = "Get original url")
    @GetMapping("/{hash}")
    public ResponseEntity<Void> getOriginalUrl(@PathVariable String hash) {
        String targetUrl = urlService.getOriginalUrl(hash);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(targetUrl)).build();
    }
}
