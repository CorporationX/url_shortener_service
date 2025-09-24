package faang.school.urlshortenerservice.controller.url;

import faang.school.urlshortenerservice.dto.CreateUrlRequest;
import faang.school.urlshortenerservice.dto.UrlResponse;
import faang.school.urlshortenerservice.service.url.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1/url")
@RequiredArgsConstructor
@Tag(name = "URL Shortener", description = "API for shortening and resolving URLs")
public class UrlController {

    private final UrlService urlService;

    @Operation(
            summary = "Create a short URL",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Short URL created"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping
    public ResponseEntity<UrlResponse> createShortUrl(@RequestBody @Valid CreateUrlRequest dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new UrlResponse(URI.create(urlService.create(dto))));
    }

    @Operation(
            summary = "Redirect to original URL",
            responses = {
                    @ApiResponse(responseCode = "302", description = "Redirect to original URL"),
                    @ApiResponse(responseCode = "404", description = "Hash not found")
            }
    )
    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirect(@PathVariable String hash) {
        String longUrl = urlService.getOriginalUrl(hash);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(longUrl))
                .build();
    }
}
