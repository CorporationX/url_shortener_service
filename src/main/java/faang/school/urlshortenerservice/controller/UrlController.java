package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/{hash}")
    public ResponseEntity<String> redirect(@PathVariable("hash") @NotNull String hash){
        String result = urlService.getOriginalUrl(hash);
        return ResponseEntity.ok(result);
    }
}
