package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.annotation.RateLimited;
import faang.school.urlshortenerservice.dto.CreateUrlRequest;
import faang.school.urlshortenerservice.dto.CreateUrlResponse;
import faang.school.urlshortenerservice.service.RedirectValidator;
import faang.school.urlshortenerservice.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "URL Shortener", description = "API for creating short URLs and redirecting to original URLs")
public class UrlController {

    private final UrlService urlService;
    private final RedirectValidator redirectValidator;

    @Operation(
            summary = "Create short URL",
            description = "Creates a shortened URL from the provided original URL. Returns a short URL that can be used for redirection."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Short URL created successfully",
                    content = @Content(schema = @Schema(implementation = CreateUrlResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request - URL validation failed"
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests - rate limit exceeded"
            )
    })
    @PostMapping("/url")
    @RateLimited(capacity = 10, refillTokens = 10, refillDurationSeconds = 60)
    public ResponseEntity<CreateUrlResponse> createShortUrl(
            @Parameter(description = "Request containing the original URL to shorten", required = true)
            @Valid @RequestBody CreateUrlRequest request) {
        log.info("Received request to create short URL for: {}", request.getUrl());

        String shortUrl = urlService.createShortUrl(request.getUrl());

        CreateUrlResponse response = new CreateUrlResponse(shortUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Redirect to original URL",
            description = "Redirects to the original URL associated with the provided hash. Returns HTTP 302 Found with Location header."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "302",
                    description = "Redirect to original URL"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "URL not found for the provided hash"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid redirect URL (security validation failed)"
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests - rate limit exceeded"
            )
    })
    @GetMapping("/{hash}")
    @RateLimited(capacity = 1000, refillTokens = 1000, refillDurationSeconds = 60)
    public ResponseEntity<Void> redirectToOriginalUrl(
            @Parameter(description = "Hash of the short URL", required = true, example = "abc123")
            @PathVariable String hash) {
        log.debug("Redirect request for hash: {}", hash);
        
        String originalUrl = urlService.getUrlByHash(hash);
        
        if (originalUrl == null || originalUrl.isEmpty()) {
            log.warn("URL not found for hash: {}", hash);
            throw new faang.school.urlshortenerservice.exception.UrlNotFoundException("URL not found for hash: " + hash);
        }

        log.debug("Found URL for hash {}: {}", hash, originalUrl);

        try {
            redirectValidator.validateRedirectUrl(originalUrl);
        } catch (faang.school.urlshortenerservice.exception.InvalidUrlException e) {
            log.error("Redirect validation failed for hash {}: {}", hash, e.getMessage());
            throw e;
        }

        log.debug("Redirecting hash {} to: {}", hash, originalUrl);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}

