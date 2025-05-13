package faang.school.urlshortenerservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Url Shortener", description = "API for managing short URLs")
@Validated
public interface UrlController {

    @Operation(
            summary = "Save a new URL and generate a hash",
            description = "Accepts a long URL and returns a generated hash",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Hash generated successfully",
                            content = @Content(
                                    mediaType = "text/plain",
                                    examples = @ExampleObject(value = "aB12Cd")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input",
                            content = @Content(
                                    mediaType = "text/plain",
                                    examples = @ExampleObject(value = """
                                            {
                                                "message": "saveUrl.url: не должно быть пустым",
                                                "status": 400,
                                                "method": "POST",
                                                "path": "/api/v1/urls?url=",
                                                "timestamp": "2025-05-13T15:04:35.132750Z"
                                            }
                                            """)
                            )
                    )
            }
    )
    @PostMapping
    ResponseEntity<String> save(
            @Parameter(
                    description = "Original long URL to be shortened",
                    example = "https://example.com/very/long/page",
                    required = true
            )
            @RequestParam @NotBlank String url
    );

    @Operation(
            summary = "Get original URL by hash",
            description = "Returns the original URL associated with the hash",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Original URL found",
                            content = @Content(
                                    mediaType = "text/plain",
                                    examples = @ExampleObject(value = "https://example.com/very/long/page")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "URL not found",
                            content = @Content(
                                    mediaType = "text/plain",
                                    examples = @ExampleObject(value = """
                                            {
                                                "message": "url by hash aB12Cd does not exists",
                                                "status": 404,
                                                "method": "GET",
                                                "path": "/api/v1/urls?hash=aB12Cd",
                                                "timestamp": "2025-05-13T14:55:05.621094Z"
                                            }
                                            """)
                            )
                    )
            }
    )
    @GetMapping
    ResponseEntity<String> get(
            @Parameter(
                    description = "Short hash to look up the original URL",
                    required = true,
                    example = "aB12Cd"
            )
            @RequestParam @NotBlank String hash
    );

    @Operation(
            summary = "Get short hash for a given URL",
            description = "Returns the existing short hash if URL is already shortened",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Hash returned successfully",
                            content = @Content(
                                    mediaType = "text/plain",
                                    examples = @ExampleObject(value = "aB12Cd")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "URL not found",
                            content = @Content(
                                    mediaType = "text/plain",
                                    examples = @ExampleObject(value = """
                                            {
                                                "message": "url https://example.com/very/long/page does not exists",
                                                "status": 404,
                                                "method": "GET",
                                                "path": "/api/v1/urls/hash?url=https://example.com/very/long/page",
                                                "timestamp": "2025-05-13T14:55:05.621094Z"
                                            }
                                            """)
                            )
                    )
            }
    )
    @GetMapping("/hash")
    ResponseEntity<String> getHash(
            @Parameter(
                    description = "Original long URL to retrieve its hash",
                    required = true,
                    example = "https://example.com/very/long/page"
            )
            @RequestParam @NotBlank String url
    );
}
