package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/urls")
@RestController
public class UrlController {

    private final UrlService urlService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public String createShortUrl(@Valid @RequestBody UrlDto urlDto) {
        return urlService.createShortUrl(urlDto);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable @Length(max = 6, message = "Short URL is too long") String hash) {
        String originalUrl = urlService.getOriginalUrl(hash);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", originalUrl)
                .build();
    }
}
