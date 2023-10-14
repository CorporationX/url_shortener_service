package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @GetMapping("/{shortUrl}")
    @ResponseStatus(HttpStatus.FOUND)
    public String redirectToLongUrl(@PathVariable String shortUrl) {
        String longUrl =  urlService.redirectToLongUrl(shortUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(longUrl));
        return new  ResponseEntity<>(headers, HttpStatus.FOUND).toString();
    }
}
