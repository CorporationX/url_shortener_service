package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ShortUrlRequestDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/url")
@SuppressWarnings("unused")
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public String getShortUrl(@Valid @RequestBody ShortUrlRequestDto requestDto) {
        return urlService.getShortUrl(requestDto);
    }
}
