package faang.school.url_shortener_service.controller;

import faang.school.url_shortener_service.dto.URLRequestDto;
import faang.school.url_shortener_service.dto.URLResponseDto;
import faang.school.url_shortener_service.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/url")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "URL Controller", description = "Handles URL shortening and retrieval of original URLs")
public class UrlController {
    private final UrlService urlService;

    @Operation(
            summary = "Create short URL",
            description = "Generates a shortened URL using Base62 encoding from a given long URL.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Short URL created successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = URLResponseDto.class),
                                    examples = @ExampleObject(value = "{\"shortUrl\":\"https://short.ly/abc123\"}"))),
                    @ApiResponse(responseCode = "400",
                            description = "Invalid request due to validation errors",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(type = "object"),
                                    examples = @ExampleObject(value = "{\"url\":\"must not be blank\"}"))),
                    @ApiResponse(responseCode = "409",
                            description = "Conflict - URL already exists",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(type = "object"),
                                    examples = @ExampleObject(value = "{\"error\": \"The URL already exists in the system.\"}")))
            })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "DTO containing the original URl to shorten",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = URLRequestDto.class),
                    examples = @ExampleObject(value = "{originalUrl\": \"https//example.com/long-url\"}")
            ))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public URLResponseDto createShortURL(@Validated @RequestBody URLRequestDto requestDto) {
        URI uri = URI.create(urlService.createShortUrl(requestDto));
        String baseUrl = uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort() + uri.getPath();
        return new URLResponseDto(baseUrl + "/" + requestDto.getHash());
    }

    @Operation(
            summary = "Retrieve original URL",
            description = "Finds the original URL using the provided hash and redirects the user.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "302",
                            description = "Redirect to original URL",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(value = "{\"massage\": Redirecting to original URL\"}")
                            )),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Hash not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(type = "object"),
                                    examples = @ExampleObject(value = "{\"error\": \"Shortened URL hash not found\" }")
                            ))
            })
    @Parameter(
            name = "hash",
            in = ParameterIn.PATH,
            description = "The hash of the shortened URL",
            required = true,
            example = "abc123")
    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public RedirectView getOriginalURL(@PathVariable("hash") String hash) {
        return new RedirectView(urlService.getOriginalURL(hash));
    }
}