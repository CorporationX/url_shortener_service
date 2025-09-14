package faang.school.urlshortenerservice.controller;


import faang.school.urlshortenerservice.dto.CreateUrlDto;
import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.dto.NewUrlResponseDto;
import faang.school.urlshortenerservice.service.hash.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/v1/url")
@RequiredArgsConstructor
@Validated
@Tag(name = "URL")
public class UrlController {

    private final UrlService urlService;

    @Operation(summary = "Convert long URL into short one")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NewUrlResponseDto post(@Valid @RequestBody CreateUrlDto createUrlDto) {
        return urlService.createShort(createUrlDto);
    }

    @Operation(summary = "Get redirect to original link by hash")
    @GetMapping("/{hash}")
    public ResponseEntity<Void> get(@Valid @ModelAttribute HashDto hashDto) {
        String originalUrl = urlService.getOriginal(hashDto);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
