package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.model.dto.UrlResponse;
import faang.school.urlshortenerservice.service.UrlService;
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
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "URL Controller", description = "Creates short urls and retrieves original urls")
public class UrlController {
    private final UrlService urlService;

    @Operation(summary = "Create short url", description = "Creates short url based on Base62")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful creates short url",
                    content = @Content(schema = @Schema(implementation = UrlResponse.class), mediaType = MediaType.TEXT_PLAIN_VALUE)),
            @ApiResponse(responseCode = "400", description = "Exists validation errors",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(type = "object", additionalProperties = Schema.AdditionalPropertiesValue.TRUE),
                            examples = @ExampleObject(value = "{\"url\":\"must not be blank\"}"))),
            @ApiResponse(responseCode = "409", description = "Url already exists")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dto for long Url", required = true)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UrlResponse createShortUrl(@Validated @RequestBody UrlDto urlDto) {
        URI uri = URI.create(urlService.createShortUrl(urlDto));
        String baseUrl = uri.getScheme() + "://" + uri.getHost();
        return new UrlResponse(baseUrl + "/" + urlDto.getHash());
    }

    @Operation(summary = "Get URL", description = "Provide original URL, basing on hash")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Successful redirect to original link"),
            @ApiResponse(responseCode = "404", description = "Hash not found")
    })
    @Parameter(name = "hash",in = ParameterIn.PATH, description = "The hash of the shortened URL", example = "abc123")
    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public RedirectView getOriginalUrl(@PathVariable("hash") String hash) {
        return new RedirectView(urlService.getOriginalUrl(hash));
    }
}
