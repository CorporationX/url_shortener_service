package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.RequestUrlDto;
import faang.school.urlshortenerservice.dto.ResponseUrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.api.version}/urls")
@Validated
@Slf4j
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<ResponseUrlDto> shorten(@Valid @RequestBody RequestUrlDto requestUrlDto) {
        log.info("Received request to shorten URL: {}", requestUrlDto.getUrl());
        ResponseUrlDto response = urlService.shorten(requestUrlDto);
        log.info("Successfully shortened URL. Generated short URL: {}", response.getShortUrl());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirect(
            @PathVariable
            @Pattern(regexp = "^[0-9A-Za-z]{6}$", message = "Invalid hash format")
            String hash) {
        log.info("Received redirect request for hash: {}", hash);
        ResponseUrlDto response = urlService.getOriginalUrl(hash);
        log.info("Redirecting hash: {} to URL: {}", hash, response.getOriginalUrl());
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, response.getOriginalUrl())
                .build();
    }
}