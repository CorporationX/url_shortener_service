package faang.school.urlshortenerservice.controller;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public UrlDto createUrl(@RequestBody UrlDto urlDto) {
        URI uri = converToUri(urlDto.getUrl());
        urlService.generateUrl(uri);

        return new UrlDto();
    }

    private URI converToUri(String stringUrl) {
        try {
            URI uri = new URL(stringUrl).toURI();
            return uri;
        } catch (URISyntaxException | MalformedURLException e) {
            log.error(stringUrl + " is not a valid url", e);
            throw new IllegalArgumentException("Invalid URL");
        }
    }
}
