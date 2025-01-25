package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/url")
@Slf4j
@Data
@RequiredArgsConstructor
@Validated
public class UrlController {
    private final UrlService urlService;

    @PostMapping()
    public String createShortUrl(@RequestBody @Valid UrlDto urlDto) {
        log.info("Received a new request to create a short URL: " + urlDto.getUrl());
        return urlService.createShortUrl(urlDto);
    }
}
