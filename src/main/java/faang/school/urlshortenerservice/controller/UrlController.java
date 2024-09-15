package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validate.ValidUrl;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Evgenii Malkov
 */
@Validated
@RestController
@RequestMapping
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String generateShortUrl(@RequestParam @ValidUrl String url) {
        return urlService.generateShortUrl(url);
    }

    @GetMapping("{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public String getUrl(@PathVariable @NotBlank String hash) {
        return urlService.getOriginalUrl(hash);
    }
}
