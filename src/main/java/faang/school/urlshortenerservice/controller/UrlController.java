package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/url-shortener")
@Slf4j
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/{hash}")
    public ResponseEntity<Void> getUrl(@PathVariable(name = "hash") String hash) {
        String url = urlService.getUrl(hash);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }

    @PostMapping("/url")
    public ResponseEntity<UrlDto> shortenUrl(@RequestBody UrlDto url) {
        UrlDto urlDto = urlService.shortenUrl(url);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(urlDto.getUrl())
                .toUri();
        return ResponseEntity.created(location).body(urlDto);
    }

}
