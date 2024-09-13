package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController()
@RequiredArgsConstructor
@Validated
public class UrlController {
    private final UrlService urlService;
    private final RestTemplate restTemplate;

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public int createUrl(@PathVariable String hash) {
        String url = urlService.findUrl(hash).getUrl();
        return redirectToUrl(url).getStatusCode().value();
    }

    @PostMapping("/url")
    public UrlDto createShortUrl(@RequestBody UrlDto url) {
        return urlService.convertToShortUrl(url);
    }

    private ResponseEntity<String> redirectToUrl(String url) {
        return restTemplate.getForEntity(url, String.class);
    }

}
