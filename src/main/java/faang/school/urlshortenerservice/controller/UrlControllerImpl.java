package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/urls") // todo add example for success request before create a tests
public class UrlControllerImpl implements UrlController{
    private final UrlService urlService;

    @Override
    public ResponseEntity<String> save(String url) {
        return ResponseEntity.ok().body(urlService.save(url));
    }

    @Override
    public ResponseEntity<String> get(String hash) {
        return ResponseEntity.ok().body(urlService.get(hash));
    }

    @Override
    public ResponseEntity<String> getHash(String url) {
        return ResponseEntity.ok().body(urlService.getHash(url));
    }
}
