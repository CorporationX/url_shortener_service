package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ShortUrlResponse;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping(UrlController.BASE_PATH)
@Slf4j
public class UrlController {

    public static final String BASE_PATH = "/api/v1/url-shortener";
    private static final String HASH_PATH = "/{hash}";
    private static final String URL_PATH = "/url";

    private final UrlService urlService;

    @GetMapping(HASH_PATH)
    @Operation(
            summary = "Redirect to original URL",
            description = "Redirects to the original long URL associated with the provided hash"
    )
    public RedirectView getOriginalUrl(@PathVariable String hash) {
        log.info("Received hash {} for url", hash);
        String url = urlService.getUrl(hash).url();
        log.info("Found url {} for hash {}", url, hash);
        return new RedirectView(url);
    }

    @PostMapping(URL_PATH)
    @Operation(
            summary = "Create short URL",
            description = "Accepts the original long URL and returns a hash and the corresponding short URL"
    )
    public ResponseEntity<ShortUrlResponse> createShortUrl(@Valid @RequestBody UrlDto url) {
        log.info("Received request to shorten URL: {}", url.url());

        String hash = urlService.createShortUrl(url);

        String shortUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(BASE_PATH)
                .path("/" + hash)
                .toUriString();

        log.info("Created short URL: {}", shortUrl);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ShortUrlResponse(hash, shortUrl));
    }
}
