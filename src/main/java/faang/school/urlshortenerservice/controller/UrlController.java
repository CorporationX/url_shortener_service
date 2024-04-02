package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDtoRequest;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/url")
public class UrlController {
    private final UrlService urlService;
    @Value("${url.address}")
    private String outUrl;

    @PostMapping
    public ResponseEntity<String> getUrl(@RequestBody UrlDtoRequest request) {
        String hash = urlService.createShortUrl(request.url());
        String fullUrlWithHash = outUrl + "/" + hash;
        URI location = URI.create(fullUrlWithHash);

        return ResponseEntity.created(location).body(fullUrlWithHash);
    }
}
