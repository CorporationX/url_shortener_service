package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.url.UrlServiceImpl;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/url")
@RestController
public class UrlController {
    private final UrlServiceImpl urlService;

    @PostMapping
    public Mono<ResponseEntity<UrlDto>> shortUrl(@RequestParam("url") @URL String url) {
        return urlService.shortenUrl(url).map(ResponseEntity::ok);
    }

    @GetMapping("/{hash}")
    public Mono<ResponseEntity<Void>> redirect(@PathVariable String hash) {
        return urlService.getOriginalUrl(hash)
                .map(url -> ResponseEntity
                        .status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, url)
                        .build()
                );
    }
}
