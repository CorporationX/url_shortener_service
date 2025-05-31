package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.service.hash.HashService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class HashController {
    private final HashService hashService;

    @PostMapping
    public UrlResponseDto createShortUrl(@Valid @RequestBody UrlRequestDto urlRequestDto) {
        return hashService.createShortUrl(urlRequestDto);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<String> redirectToOriginalUrl(@Valid @NotBlank @PathVariable String hash) {
        return hashService.redirectToOriginalUrl(hash);
    }
}
