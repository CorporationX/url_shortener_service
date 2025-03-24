package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.request.UrlRequestDto;
import faang.school.urlshortenerservice.dto.response.HashResponseDto;
import faang.school.urlshortenerservice.service.UrlServiceImpl;
import faang.school.urlshortenerservice.service.api.UrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Validated
@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<HashResponseDto> generateShortUrl(@Valid @RequestBody UrlRequestDto urlRequestDto) {
        String hash = urlService.generateShortUrl(urlRequestDto.url());
        return ResponseEntity.ok(new HashResponseDto(hash));
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToUrl(@PathVariable @NotBlank String hash) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(urlService.getUrl(hash)))
                .build();
    }
}
