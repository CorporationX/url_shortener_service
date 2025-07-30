package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.url.UrlRequestDto;
import faang.school.urlshortenerservice.dto.url.UrlResponseDto;
import faang.school.urlshortenerservice.service.UrlHashCacheService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/url")
public class UrlController {

    private final UrlHashCacheService urlHashCacheService;

    @GetMapping("/{hash}")
    public ResponseEntity<URI> getByHash(@PathVariable("hash") String hash) {
        UrlResponseDto urlResponseDto = urlHashCacheService.getShortUrl(hash);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(urlResponseDto.getUrlResponseDto().toString()))
                .body(urlResponseDto.getUrlResponseDto());
    }

    @PostMapping
    public ResponseEntity<UrlResponseDto> createShortUrl(@Valid @RequestBody UrlRequestDto createUrlDto) {
        UrlResponseDto urlResponseDto = urlHashCacheService.createShortUrl(createUrlDto);
        return ResponseEntity.created(urlResponseDto.getUrlResponseDto())
                .body(urlResponseDto);
    }
}
