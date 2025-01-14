package faang.school.urlshortenerservice.controller;


import faang.school.urlshortenerservice.dto.UrlRequestDTO;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequestMapping("/url")
@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;
    @PostMapping
    public ResponseEntity<String> createShortUrl(@Valid @RequestBody UrlRequestDTO urlRequestDTO) {
        String shortUrl = urlService.createShortUrl(urlRequestDTO.getLongUrl());
        return ResponseEntity.ok(shortUrl);
    }
}
