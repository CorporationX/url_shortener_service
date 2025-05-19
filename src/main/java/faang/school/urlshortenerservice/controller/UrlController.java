package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.dto.RequestUrlDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Url Shortener", description = "API for managing short URLs")
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
    ResponseEntity<HashDto> save(
            @Parameter(
                    description = "Original long URL to be shortened",
                    example = "https://example.com/very/long/page",
                    required = true
            )
            @RequestParam @NotBlank RequestUrlDto url
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
    ResponseEntity<Void> get(
            @Parameter(
                    description = "Short hash to look up the original URL",
                    required = true,
                    example = "aB12Cd"
            )
            @PathVariable @NotBlank HashDto hash
    );
}
