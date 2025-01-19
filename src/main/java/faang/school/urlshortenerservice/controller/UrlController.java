package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/url")
@Data
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<String> createShortUrl(@Validated @RequestBody UrlRequestDto urlRequestDto) {
        String shortUrl = urlService.createShortUrl(urlRequestDto.getLongUrl());
        return ResponseEntity.ok(shortUrl);
    }
}
