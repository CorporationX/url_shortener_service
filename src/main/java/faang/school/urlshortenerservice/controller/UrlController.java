package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/shortener")
@Validated
public class UrlController {
    private final UrlService urlService;

    @PostMapping()
    public ResponseEntity<Void> createUrl(@Valid @RequestBody @NotNull UrlDto urlDto) {
        urlService.saveNewHash(urlDto);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> getUrl(@PathVariable @NotNull String hash) {
        String url = Optional.ofNullable(urlService.findUrl(hash))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "URL not found"));

        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, url)
                .build();
    }
}
