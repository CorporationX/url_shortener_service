package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.response.UrlResponse;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@Validated
@RequestMapping("/api/v1/")
public class UrlController {
    private final UrlService urlService;

    @GetMapping("/{hash}")
    public ResponseEntity<?> getUrl(@PathVariable String hash) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", urlService.getUrl(hash))
                .build();
    }

    @PostMapping("/url")
    public UrlResponse save(@RequestBody @Valid UrlDto urlDto) {
        return urlService.save(urlDto);
    }
}
