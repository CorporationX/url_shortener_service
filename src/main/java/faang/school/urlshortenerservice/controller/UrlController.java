package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.url.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<UrlDto> shorten(@RequestBody UrlDto urlDto) throws JsonProcessingException {
        UrlDto url = urlService.createShortUrl(urlDto.getUrl());
        return ResponseEntity.ok(url);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Object> redirectToUrl(@PathVariable String hash) throws JsonProcessingException {
        String url = urlService.getUrl(hash);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
    }

}
