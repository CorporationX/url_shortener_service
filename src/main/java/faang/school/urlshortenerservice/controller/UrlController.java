package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/urls")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/short")
    @ResponseStatus(HttpStatus.CREATED)
    public HashDto saveUrl(@RequestBody @Valid UrlDto urlDto) {
        return urlService.createShortLink(urlDto);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> getUrlByHash(@PathVariable String hash) {
        String url = urlService.getUrlByHash(hash);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
