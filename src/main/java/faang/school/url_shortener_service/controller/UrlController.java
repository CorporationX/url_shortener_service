package faang.school.url_shortener_service.controller;

import faang.school.url_shortener_service.config.swagger.annotations.ApiCreateShortUrl;
import faang.school.url_shortener_service.config.swagger.annotations.ApiGetOriginalUrl;
import faang.school.url_shortener_service.dto.UrlRequestDto;
import faang.school.url_shortener_service.dto.UrlResponseDto;
import faang.school.url_shortener_service.service.UrlService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/url")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "URL Controller", description = "Handles URL shortening and retrieval of original URLs")
public class UrlController {
    private final UrlService urlService;

    @ApiCreateShortUrl
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UrlResponseDto createShortURL(@Validated @RequestBody UrlRequestDto requestDto) {
        return urlService.createShortUrl(requestDto);
    }

    @ApiGetOriginalUrl
    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<Void> redirect(@PathVariable("hash") String hash) {
        String longUrl = urlService.getOriginalURL(hash);
        return ResponseEntity.status(302).header("Location", longUrl).build();
    }
}