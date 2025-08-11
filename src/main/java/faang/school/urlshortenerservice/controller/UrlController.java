package faang.school.urlshortenerservice.controller;


import faang.school.urlshortenerservice.dto.ResponseDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public ResponseEntity<ResponseDto> createShortUrl(@Valid @RequestBody UrlDto urlRequest) {
        return ResponseEntity.ok(urlService.createShortUrl(urlRequest));
    }

    @GetMapping("/{hash}")
    public ResponseEntity<String> getUrl(@PathVariable String hash) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Location", urlService.getUrl(hash));
        return ResponseEntity.status(302).headers(responseHeaders).body("REDIRECT");
    }
}
