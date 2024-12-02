package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.url.UrlService;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Validated
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public String createHashUrl(@RequestParam(name = "url") @NotEmpty @URL String url) {
        return urlService.createHashUrl(url);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirect(@PathVariable String hash) {
        String primalUri = urlService.getPrimalUri(hash);
        return ResponseEntity
                .status(302)
                .header("Location", primalUri)
                .build();
    }
}
