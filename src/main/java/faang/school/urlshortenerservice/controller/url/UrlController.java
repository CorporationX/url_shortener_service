package faang.school.urlshortenerservice.controller.url;

import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.service.url.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/url")
public class UrlController {

    @Value("${url.shortener.prefix:localhost:8090/url/}")
    private String prefix;
    private final UrlService urlService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String shortenUrl(@RequestBody @Valid UrlDto urlDto) {
        return prefix + urlService.shortenUrl(urlDto);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> getOriginalUrl(@PathVariable String hash) {
        return ResponseEntity.status(HttpStatus.valueOf(302))
                .location(java.net.URI.create(urlService.getOriginalUrl(hash)))
                .build();
    }
}
