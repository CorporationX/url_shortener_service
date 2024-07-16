package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validator.UrlValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {

    @Value("${url-path.path}")
    private String path;

    private final UrlValidator urlValidator;
    private final UrlService urlService;

    @PostMapping("/createShortUrl")
    @ResponseStatus(HttpStatus.CREATED)
    public String createShortUrl(@RequestBody String url) {
        urlValidator.validateUrl(url);
        String hash = urlService.createShortUrl(url);
        return path + hash;
    }

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> redirectToOriginal(@PathVariable String hash) {
        String originalUrl = urlService.getOriginalUrl(hash);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(originalUrl));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
