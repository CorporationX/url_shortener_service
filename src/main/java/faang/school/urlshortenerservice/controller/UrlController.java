package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/url")
    public ResponseEntity<UrlDto> shortenUrl(@Valid @RequestBody UrlDto urlDto, HttpServletRequest request) {
        String hash = urlService.shortenUrl(urlDto.getUrl());
        String fullUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath("/" + hash)
                .build()
                .toUriString();
        UrlDto dto = new UrlDto(fullUrl);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> getUrlByHash(@PathVariable String hash) {
        String url = urlService.getUrlByHash(hash);
        log.info("getUrlByHash: " + url);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", url);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

}