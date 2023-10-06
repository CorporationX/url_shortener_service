package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/url")
public class UrlController {
    private final UrlService urlService;

    @PostMapping({"/url"})
    public void getShortUrl(UrlDto urlDto) throws MalformedURLException {
        isValid(urlDto.getUrl());
        urlService.getShortUrl(urlDto);
    }
    private boolean isValid(String url) throws MalformedURLException {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            throw new MalformedURLException("Invalid url " + url);
        }
    }
}
