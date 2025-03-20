package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/url")
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@Valid @RequestBody UrlRequestDto requestDto) {
        String shortUrl = urlService.shorten(requestDto);
        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("/{hash}")
    public String redirectToOriginalUrl(@PathVariable("hash") String hash) {
        try {
            String url = urlService.findUrlByHash(hash);
            return "redirect:" + url;
        } catch (UrlNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
