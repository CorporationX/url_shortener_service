package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.service.url.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/urls")
@Validated
public class UrlControllerImpl implements UrlController{
    private final UrlService urlService;

    @PostMapping("/url")
    @Override
    public ResponseEntity<HashDto> save(String url) {
        return ResponseEntity.ok().body(urlService.save(url));
    }

    @GetMapping("/{hash}")
    @Override
    public ResponseEntity<Void> get(HashDto hash) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(urlService.get(hash.getHash())))
                .build();
    }
}
