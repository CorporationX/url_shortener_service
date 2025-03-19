package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.validator.UrlValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;

@RequiredArgsConstructor
@RequestMapping("/url")
@RestController
public class UrlController {
    private final UrlValidator urlValidator;

    @PostMapping("/create")
    public ResponseEntity<String> createShortUrl(UrlDto dto) {
        URL url = urlValidator.validateUrl(dto);

        return ResponseEntity.ok(null);
    }
}
