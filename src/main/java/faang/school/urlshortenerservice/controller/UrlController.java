package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.RequestDto;
import faang.school.urlshortenerservice.dto.HashResponseDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hash")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public ResponseEntity<HashResponseDto> getHash(@RequestBody @Valid RequestDto dto) {
        return ResponseEntity.ok(urlService.getHash(dto));
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> getUrl(@PathVariable("hash") String hash) {
        String longUrl = urlService.getUrl(hash);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, longUrl)
                .build();
    }

}
