package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ShortUrlResponseDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/url")
public class UrlController {

    private final UrlService urlService;

    @Value("${server.base-url}")
    private String baseUrl;

    @PostMapping
    public ShortUrlResponseDto createShortUrl(@Valid @RequestBody UrlDto urlDto) {
        String hash = urlService.createShortUrl(urlDto.getUrl());
        return ShortUrlResponseDto.builder()
                .shortUrl(baseUrl + "/" + hash)
                .build();
    }

    @GetMapping("/{hash}")
    public void getOriginalUrl(@PathVariable String hash, HttpServletResponse response) throws IOException {
        String originalUrl = urlService.getOriginalUrl(hash)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "URL not found for hash: " + hash
                ));
        response.sendRedirect(originalUrl);
    }
}