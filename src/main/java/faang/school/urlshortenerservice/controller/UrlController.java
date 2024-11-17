package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlShortener;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlShortener urlShortener;

    @PostMapping("/url")
    public String shortenUrl(@RequestBody String url) {
        return urlShortener.shortenUrl(url);
    }

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.PERMANENT_REDIRECT)
    public String getUrl(@PathVariable @Size(min = 6, max = 6) String hash) {
        return urlShortener.getUrl(hash);
    }
}
