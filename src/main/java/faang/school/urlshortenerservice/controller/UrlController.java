package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
@RequiredArgsConstructor
@Slf4j
@Validated
public class UrlController {
    public final UrlService urlService;

    @PostMapping
    public ResponseEntity<ShortUrlDto> createShortUrl(@Valid @RequestBody LongUrlDto longUrlDto) {
        log.info("Requesting short URL for {}", longUrlDto);
        return ResponseEntity.status(HttpStatusCode.valueOf(302)).body(urlService.createShortUrl(longUrlDto));
    }

    @GetMapping("/{hash}")
    public ResponseEntity<LongUrlDto> getLongUrl(@PathVariable
                                                 @Size(min = 6, max = 6, message = "Hash must contain 6 chars.") String hash) {
        String longUrl = urlService.getLongUrl(hash);
        return ResponseEntity.status(HttpStatusCode.valueOf(302))
                .header(HttpHeaders.LOCATION, longUrl)
                .build();
    }
}
