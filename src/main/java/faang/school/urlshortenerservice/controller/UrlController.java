package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/urls")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @Operation(summary = "Create a short URL", description = "Accepts a long URL and returns a generated hash for the shortened URL.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Short URL successfully created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HashDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @PostMapping("/short")
    @ResponseStatus(HttpStatus.CREATED)
    public HashDto saveUrl(@RequestBody @Valid UrlDto urlDto) {
        return urlService.createShortLink(urlDto);
    }

    @Operation(summary = "Get the long URL by hash", description = "Looks up the long URL associated with the provided hash and redirects to it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirect to the original URL"),
            @ApiResponse(responseCode = "404", description = "URL not found", content = @Content)
    })
    @GetMapping("/{hash}")
    public ResponseEntity<Void> getUrlByHash(
            @Parameter(description = "The hash of the shortened URL") @PathVariable String hash) {
        String url = urlService.getUrlByHash(hash);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
