package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${base.url}/url")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<UrlResponseDto> createShortUrl(@Valid @RequestBody UrlRequestDto urlRequest) {
        String shortUrl = urlService.createShortUrl(urlRequest);
        return ResponseEntity.ok(new UrlResponseDto(shortUrl));
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> getLongUrl(@PathVariable String hash) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, urlService.getLongUrl(hash));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}