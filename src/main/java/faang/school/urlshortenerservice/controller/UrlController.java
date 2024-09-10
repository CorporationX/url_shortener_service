package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.net.URL;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public String createHash(@Validated @RequestBody UrlDto urlDto) {

        String url = urlDto.getOriginalUrl();
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            log.error("Incorrect URL: {}", url);
            throw new RuntimeException(e);
        }
        log.info("Valid URL: {}", url);
        return urlService.shorten(urlDto);
    }
}
