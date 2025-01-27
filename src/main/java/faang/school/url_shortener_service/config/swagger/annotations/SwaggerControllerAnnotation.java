package faang.school.url_shortener_service.config.swagger.annotations;

import faang.school.url_shortener_service.dto.UrlRequestDto;
import faang.school.url_shortener_service.dto.UrlResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

public interface SwaggerControllerAnnotation {
    @Operation(
            summary = "Create short URL",
            description = "Generates a shortened URL using Base62 encoding from a given long URL."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Short URL created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UrlResponseDto.class),
                            examples = @ExampleObject(value = "{\"originalUrl\":\"https://short.ly/abc123\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid request due to validation errors",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "object"),
                            examples = @ExampleObject(value = "{\"originalUrl\":\"must not be blank\"}")))
    })
    UrlResponseDto createShortURL(UrlRequestDto requestDto);

    @Operation(
            summary = "Retrieve original URL",
            description = "Finds the original URL using the provided hash and redirects the user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Redirect to original URL",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Redirecting to original URL\"}"))),
            @ApiResponse(responseCode = "404", description = "Hash not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "object"),
                            examples = @ExampleObject(value = "{\"error\": \"Shortened URL hash not found\"}")))
    })
    @Parameter(name = "hash", in = ParameterIn.PATH, description = "The hash of the shortened URL", required = true, example = "abc123")
    ResponseEntity<Void> redirect(String hash);
}