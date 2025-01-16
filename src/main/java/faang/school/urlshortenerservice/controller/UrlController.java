package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
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
@RequestMapping("/url")
@RequiredArgsConstructor
@Validated
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public String getShortUrl(@Valid @RequestBody UrlDto urlDto) {
        return urlService.getShortUrl(urlDto);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String hash) {
        String originalUrl = urlService.redirectToRealUrl(hash);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.LOCATION, originalUrl);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
