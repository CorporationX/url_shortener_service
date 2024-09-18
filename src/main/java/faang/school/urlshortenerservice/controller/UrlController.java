package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.service.Validator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class UrlController {

    private final Validator validator;
    private final UrlService urlService;

    @PostMapping("/url")
    public ResponseEntity<String> createShortUrl(@Valid @RequestBody UrlDto longUrl) {
        validator.validateUrl(longUrl);
        return ResponseEntity.status(200).body(urlService.createShortUrl(longUrl));
    }

    @GetMapping("/url/{shortUrl}")
    public ResponseEntity<?> getOriginUrl(@PathVariable String shortUrl) {
        return ResponseEntity.status(302).body(urlService.getOriginUrl(shortUrl));
    }
}