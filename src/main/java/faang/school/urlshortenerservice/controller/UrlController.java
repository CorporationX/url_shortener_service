package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping
public class UrlController {

    private final UrlService urlService;

    @Operation(summary = "Converts long url to short url")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The short URL was successfully created"),
            @ApiResponse(responseCode = "500", description = "Internal error occurred on the server when creating a short URL")
    })
    @PostMapping("/url")
    public String convertToShortUrl(@RequestBody @Valid UrlDto urlDto) {
        return urlService.convertToShortUrl(urlDto);
    }

    @GetMapping("/{hash}")
    @Operation(summary = "Redirects to the original URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The original URL was found successfully"),
            @ApiResponse(responseCode = "500", description = "Internal error occurred on the server when redirecting to the original URL")
    })
    public RedirectView redirectOriginalUrl(@PathVariable String hash) {
        String url = urlService.redirectOriginalUrl(hash);
        return new RedirectView(url);
    }

}