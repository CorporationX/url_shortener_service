package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ShortenedUrlDto;
import faang.school.urlshortenerservice.dto.UrlShortenerDto;
import faang.school.urlshortenerservice.service.UrlShortenerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/urlShortener")
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    @PostMapping
    public ShortenedUrlDto shortenUrl(@RequestBody @Valid UrlShortenerDto urlShortenerDto) {
        return urlShortenerService.create(urlShortenerDto);
    }

    @Validated
    @GetMapping("/{hash}")
    public ResponseEntity<Void> findUrlByHash(@PathVariable
                                              @Pattern(regexp = "\\d{1,7}",
                                                      message = "Hash must be numeric and up to 7 digits")
                                              String hash) {
        String originalUrl = urlShortenerService.findUrlByHash(hash);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", originalUrl);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
