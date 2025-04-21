package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequestMapping("${spring.application.path}")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    @Operation(
            summary = "Create a short link", description = "Add a short link to the system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Short link created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid URL"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<String> createShortLink(@RequestBody @Valid UrlDto urlDto) {
        return ResponseEntity.ok(urlService.createShortLink(urlDto));
    }

    @GetMapping("/{hash}")
    @Operation(
            summary = "Get a URL from a short link", description = "Retrieve the user to the original link",
            responses = {
                    @ApiResponse(responseCode = "302", description = "URL for short link found"),
                    @ApiResponse(responseCode = "404", description = "URL for short link not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<Void> getUrl(@PathVariable String hash) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(urlService.getUrl(hash)))
                .build();
    }
}
