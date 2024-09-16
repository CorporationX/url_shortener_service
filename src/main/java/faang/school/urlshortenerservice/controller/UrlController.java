package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public String createUrl(@RequestParam @URL String url) {
        return urlService.createUrl(url);
    }

    @GetMapping("{hash}")
    public ResponseEntity<Void> getUrl(@PathVariable @NotBlank String hash) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(urlService.getUrl(hash)));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
