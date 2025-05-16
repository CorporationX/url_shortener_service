package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
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
@RequestMapping("api/v1/urls") // todo add example for success request before create a tests
@Validated
public class UrlControllerImpl implements UrlController{
    private final UrlService urlService;

    @PostMapping("/url")
    @Override
    public ResponseEntity<String> save(String url) {
        return ResponseEntity.ok().body(urlService.save(url));
    }
    @GetMapping("/{hash}")
    @Override
    public ResponseEntity<Void> get(String hash) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(urlService.get(hash)))
                .build();
    }
}
