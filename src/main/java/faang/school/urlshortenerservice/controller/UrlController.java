package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.RequestDto;
import faang.school.urlshortenerservice.dto.ResponseDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/url")
    public ResponseEntity<ResponseDto> shortenUrl(@RequestBody @Valid RequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(urlService.save(dto));
    }

    @GetMapping("/get")
    public ResponseEntity<Void> get(@RequestParam("hash") String hash) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(urlService.get(hash)))
                        .build();
    }
}