package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public String createShortUrl(@Valid @RequestBody UrlDto urlDto) {
        return urlService.getShortUrl(urlDto);
    }
}
