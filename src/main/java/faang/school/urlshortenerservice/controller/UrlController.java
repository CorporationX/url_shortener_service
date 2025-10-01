package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.exception.InvalidUrlException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashCache;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping
@Validated
public class UrlController {

    private final UrlService urlService;
    private static final UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"}, UrlValidator.ALLOW_LOCAL_URLS);

    @PostMapping("/url")
    public ResponseEntity<String> createLink(@RequestBody String url) {
        if(!urlValidator.isValid(url)) {
            throw new InvalidUrlException("Передан невалидный URL");
        }
        return ResponseEntity.ok(urlService.addLink(url));
    }

    @GetMapping("/{hash}")
    public ResponseEntity<String> redirect(@PathVariable @NotEmpty String hash) {
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).location(URI.create(urlService.findUrl(hash))).build();
    }

}
