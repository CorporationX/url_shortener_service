package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
@RequiredArgsConstructor
@RequestMapping
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<String> createUrl(@RequestBody @Valid UrlDto url) {
        String hash = urlService.createUrl(url);
        return ResponseEntity.status(HttpStatus.CREATED).body(hash);
    }

    @GetMapping("{hash}")
    public ResponseEntity<Void> getUrl(@PathVariable @NotBlank String hash) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(urlService.getUrl(hash)));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
