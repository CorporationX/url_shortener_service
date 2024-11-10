package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.UrlResponse;
import faang.school.urlshortenerservice.exceptions.DataValidationException;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(("/api/v1"))
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/url")
    public ResponseEntity<UrlResponse> shortenUrl(@RequestBody UrlDto urlDto) {
        validateUrl(urlDto.getUrl());
        UrlDto urlDto1 = urlService.shortenUrl(urlDto);
        return ResponseEntity.ok(new UrlResponse(urlDto1.getUrl()));
    }

    @GetMapping("/{hash}")
    public ResponseEntity<UrlResponse> getUrl(@PathVariable String hash) {
        UrlDto urlDto = urlService.getNormalUrl(hash);
        return ResponseEntity.ok(new UrlResponse(urlDto.getUrl()));
    }

    private void validateUrl(String url) {
        if (!url.contains("https://")) {
            throw new DataValidationException("Invalid URL: " + url);
        }
    }
}
