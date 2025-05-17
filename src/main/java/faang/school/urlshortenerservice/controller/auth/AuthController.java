package faang.school.urlshortenerservice.controller.auth;

import faang.school.urlshortenerservice.dto.ApiError;
import faang.school.urlshortenerservice.dto.JwtRequest;
import faang.school.urlshortenerservice.dto.JwtResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface AuthController {
    @Operation(
            summary = "Generate JWT token",
            description = "Authenticates the user using username and password, and returns a JWT token if credentials are valid.",
            requestBody = @RequestBody(
                    description = "User credentials",
                    required = true,
                    content = @Content(schema = @Schema(implementation = JwtRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Authentication successful",
                            content = @Content(schema = @Schema(implementation = JwtResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid credentials",
                            content = @Content(schema = @Schema(implementation = ApiError.class))
                    )
            }
    )
    ResponseEntity<JwtResponse> createAuthToken(
            @org.springframework.web.bind.annotation.RequestBody JwtRequest authRequest);
}
