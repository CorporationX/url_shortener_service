package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validator.UrlValidator;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/")
@RequiredArgsConstructor
public class UrlController {

    private final UrlValidator urlValidator;
    private final UrlService urlService;

    @PostMapping("/url")
    @ResponseStatus(HttpStatus.CREATED)
    public String getShortUrl(@RequestBody UrlDto url) {
        urlValidator.isValid(url);
        return urlService.getShortUrl(url);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> getFullUrl(@PathVariable @Size(min = 6, max = 6) String hash) {
        String longUrl = urlService.getFullUrl(hash);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, longUrl)
                .build();
    }
}
