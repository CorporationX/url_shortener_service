package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/url")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UrlDto createShortUrl(@Valid @RequestBody UrlDto dto) {
        return urlService.createShortUrl(dto);
    }
}
