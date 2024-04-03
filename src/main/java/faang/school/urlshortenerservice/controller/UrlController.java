package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
public class UrlController {
    private final UrlService urlService;
    @PostMapping("/url")
    public UrlDto createShortUrl (@RequestBody @Valid UrlDto urlDto) {
        System.out.println(urlDto.getUrl());
        return urlService.generateShortUrl(urlDto);
    }
}
