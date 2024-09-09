package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.URLDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shortener")
public class URLController {
    private final UrlService urlService;

    @PostMapping("/url")
    public ResponseEntity<String> createShortLink(@Valid @RequestBody URLDto urlDto){
        return ResponseEntity.status(HttpStatus.OK).body(urlService.createShortLink(urlDto));
    }
}
