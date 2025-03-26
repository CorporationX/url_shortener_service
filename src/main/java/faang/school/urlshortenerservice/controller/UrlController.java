package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/url-shortener")
@Slf4j
public class UrlController {

    private static final String HASH_PATH = "/{hash}";
    private static final String URL_PATH = "/url";

    private final UrlService urlService;

    @GetMapping(HASH_PATH)
    public RedirectView getUrl(@PathVariable(name = "hash") String hash) {
        log.info("Received hash {} for url", hash);
        String url = urlService.getUrl(hash);
        log.info("Found url {} for hash {}", url, hash);
        return new RedirectView(url);
    }

    @PostMapping(URL_PATH)
    public ResponseEntity<UrlDto> shortenUrl(@RequestBody UrlDto url) {
        log.info("Received url {} to find short url", url.getUrl());
        UrlDto urlDto = urlService.shortenUrl(url);
        log.info("Found short url {} to url {}", urlDto.getUrl(), url.getUrl());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(urlDto.getUrl())
                .toUri();
        return ResponseEntity.created(location).body(urlDto);
    }

}
