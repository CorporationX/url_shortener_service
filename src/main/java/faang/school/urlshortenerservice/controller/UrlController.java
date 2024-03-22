package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
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
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping()
    public UrlDto createShortUrl(@RequestBody @Valid UrlDto url) {
        return urlService.createShortUrl(url);
    }


    @GetMapping("{hash}")
    public ResponseEntity<Void> getUrl(@PathVariable("hash") String hash) {
        String url = urlService.getUrlByHash(hash);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(  URI.create(url)).build();
    }
}