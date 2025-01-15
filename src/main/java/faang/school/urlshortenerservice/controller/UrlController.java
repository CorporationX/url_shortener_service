package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validator.SecureUrlValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/shorten")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;
    private final SecureUrlValidator secureUrlValidator;

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @PostMapping("/url")
    public UrlDto shorten(@RequestBody UrlDto dto){
        secureUrlValidator.validate(dto.getUrl());
        return urlService.shortenUrl(dto);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirect(@PathVariable String hash) {
        String url = urlService.getUrl(hash);
        return ResponseEntity
                .status(302)
                .header(HttpHeaders.LOCATION, url)
                .build();
    }
}