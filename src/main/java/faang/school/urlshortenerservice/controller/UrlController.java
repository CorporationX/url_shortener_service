package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validator.UrlValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController("/api/v1/url")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;
    private final UrlValidator urlValidator;
    private final UrlMapper urlMapper;

    @GetMapping("/original/{hash}")
    public ResponseEntity<Void> sendToOriginalUrl(@PathVariable String hash) {
        Url originalUrl = urlService.getOriginalUrl(hash);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl.getUrl()))
                .build();
    }

    @PostMapping("/short/{longUrl}")
    public String receiveLongUrl(@PathVariable UrlDto longUrl) {
        return processResponse(longUrl);
    }

    private String processResponse(UrlDto urlDto) {
        urlValidator.isValidUrl(urlDto.getUrl());
        return urlMapper.toEntity(urlDto).getUrl();
    }
}
