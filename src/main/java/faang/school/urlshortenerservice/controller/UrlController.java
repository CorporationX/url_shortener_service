package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.service.UrlServiceImpl;
import faang.school.urlshortenerservice.validator.UrlValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlServiceImpl urlService;
    private final UrlValidator urlValidator;
    private UrlMapper urlMapper;

    @GetMapping("/api/v1/url/original/{hash}")
    public ResponseEntity<Void> sendToOriginalUrl(@PathVariable String hash) {
        Url originalUrl = urlService.getOriginalUrl(hash);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl.getUrl()))
                .build();
    }

    @PostMapping("/api/v1/url/short")
    public UrlDto convertLongUrl(@RequestBody UrlDto longUrl) {
        return urlService.convertLongUrl(processResponse(longUrl));
    }

    private Url processResponse(UrlDto urlDto) {
        urlValidator.isValidUrl(urlDto.getUrl());
        return urlMapper.toEntity(urlDto);
    }
}
