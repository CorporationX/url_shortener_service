package faang.school.urlshortenerservice.controller.url;

import faang.school.urlshortenerservice.dto.short_url.CreateShortUrlDto;
import faang.school.urlshortenerservice.dto.short_url.UrlDto;
import faang.school.urlshortenerservice.service.ShortUrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@AllArgsConstructor
@RestController
public class UrlController {
    private final ShortUrlService shortUrlService;

    @Operation(
            description = "Generate short url",
            responses = {
                    @ApiResponse(responseCode = "201"),
                    @ApiResponse(responseCode = "409", description = "Could not generate short url"),
            }
    )
    @PostMapping("/api/v1/url")
    public ResponseEntity<UrlDto> store(@RequestBody @Valid CreateShortUrlDto dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new UrlDto(URI.create(shortUrlService.create(dto))));
    }

    @Operation(
            description = "Redirect to original url",
            responses = {
                    @ApiResponse(responseCode = "301"),
                    @ApiResponse(responseCode = "404"),
                    @ApiResponse(responseCode = "422")
            }
    )
    @GetMapping("/{code}")
    public ResponseEntity<Void> get(@PathVariable("code") String code) {
        String url = shortUrlService.find(code);

        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .location(URI.create(url))
                .build();
    }
}
