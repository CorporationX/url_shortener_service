package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @Value("${}")
    private String baseUrl;

    @PostMapping
    public String shortenUrl(@RequestBody UrlDto urlDto) {
        String shortUrl = urlService.shortenUrl(urlDto);
        return baseUrl + shortUrl;
    }
}
