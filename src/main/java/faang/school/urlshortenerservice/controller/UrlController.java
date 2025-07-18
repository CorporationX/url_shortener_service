package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/url")
@Validated
public class UrlController {
    private final UrlService service;

    @PostMapping
    public String createHash(@Valid @RequestBody UrlDto urlDto) {
        return service.create(urlDto);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Object> getUrl(@PathVariable("hash") @NotEmpty(message = "hash should be not empty") String hash) {
        return ResponseEntity.
                status(302).
                body(service.find(hash));
    }
}
