package faang.school.url_shortener_service.config.swagger.annotations;

import faang.school.url_shortener_service.dto.UrlResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
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
public @interface ApiCreateShortUrl {
}
