package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Objects;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/url")
public class UrlController {
    private final UrlService urlService;

    @Operation(summary = "Create short url", description = "Creating short url and save in db")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public String createShortUrl(@RequestBody @Validated(UrlDto.Create.class) UrlDto urlDto) {
        return urlService.createShortUrl(urlDto);
    }

    //TODO asdfasdf
    @Operation(summary = "Get original url")
    @GetMapping()
    public RedirectView getOriginalUrl(@RequestParam String shortUrl) {
        String targetUrl = urlService.getOriginalUrl(shortUrl);
        return new RedirectView(Objects.requireNonNullElse(targetUrl, "/error"));
    }
}
