package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlToShortDto;
import faang.school.urlshortenerservice.service.url.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/url")
@RestController
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String generateShortUrl(@RequestBody @Valid UrlToShortDto urlToShortDto) {
        return urlService.generateShortUrl(urlToShortDto.getUrl());
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirect(@PathVariable String hash) {
        String url = urlService.getUrlByHash(hash);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
