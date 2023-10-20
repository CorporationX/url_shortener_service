package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/url")
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/")
    public ResponseEntity<String> getUrl(@RequestBody UrlDto urlDto) {
        return ResponseEntity
                .ok(urlService.getShortUrl(urlDto));
    }

    @GetMapping("/{hash}")
    public RedirectView redirectToOriginalUrl(@PathVariable String hash) {
        String originalUrl = urlService.getOriginalUrl(hash);
        return new RedirectView(originalUrl);
    }
}
