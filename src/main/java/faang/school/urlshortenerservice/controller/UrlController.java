package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/url")
@RequiredArgsConstructor
@Validated
@Tag(name = "URL Shortener API", description = "Endpoints to use the service")
public class UrlController {
    private final UrlService urlService;

    @PostMapping()
    @Operation(summary = "Convert long URL into short URL")
    public ResponseEntity<ShortUrlDto> create(@Valid @RequestBody LongUrlDto longUrlDto) {
        log.info("Request to convert long URL '{}' received", longUrlDto.url());
        return ResponseEntity.status(HttpStatusCode.valueOf(302)).body(urlService.createShortUrl(longUrlDto));
    }

    @GetMapping()
    @Operation(summary = "Get real url matching provided short url")
    public ResponseEntity<LongUrlDto> getUrl(@RequestParam(name = "shortUrl") @NotBlank(message = "URL cannot be empty") String shortUrl) {
        log.info("Request to get real URL matching short link '{}' received", shortUrl);
        return ResponseEntity.status(HttpStatusCode.valueOf(302)).body(urlService.getUrl(shortUrl));
    }
}
