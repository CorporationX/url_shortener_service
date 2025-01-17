package faang.school.urlshortenerservice.controller;


import faang.school.urlshortenerservice.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.URL;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@Tag(name = "URL-Shortener API", description = "Endpoint for operations with URL-Shortener")
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/url")
    @Operation(summary = "Create short link from original link")
    ResponseEntity<String> createShortLink(@RequestBody
                                           @URL
                                           @Parameter(description = "Original URL")
                                           String url) {
        log.info("Request to create short link for url: {}", url);
        return ResponseEntity.status(HttpStatus.CREATED).body(urlService.createShortLink(url));
    }

    @GetMapping("/{hash}")
    @Operation(summary = "Processing request to get original link")
    public ResponseEntity<Void> getOriginalLink(@PathVariable String hash) {
        log.info("Request to redirect to original url: {}", hash);
        return ResponseEntity
                .status(HttpStatus.TEMPORARY_REDIRECT)
                .header(HttpHeaders.LOCATION, urlService.getOriginalUrl(hash))
                .build();
    }
}
