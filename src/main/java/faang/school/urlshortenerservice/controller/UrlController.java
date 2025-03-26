package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "Url-Controller")
@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/{hash}")
    public ResponseEntity<Void> getUrl(@PathVariable String hash,
                                       HttpServletResponse response) throws IOException {
        String url = urlService.getOriginalUrl(hash);
        response.sendRedirect(url);
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }

    @PostMapping("/api/v1/url")
    public ResponseEntity<UrlDto> createShortLink(@Valid @RequestBody UrlDto urlDto) {
        var shortUrl = urlService.generateShortUrl(urlDto);
        return ResponseEntity.ok().body(shortUrl);
    }
}