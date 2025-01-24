package faang.school.url_shortener_service.config.swagger.annotations;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
public @interface ApiGetOriginalUrl {
}
