package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.InvalidUrlException;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.net.URL;

@RestController
@RequestMapping("api/v1/url")
@RequiredArgsConstructor
@Slf4j
public class UrlController {

    private final UrlService urlService;

    @Value("${url.shortener.address}")
    private String serverAddress;

    @PostMapping
    public UrlDto generateRedirect(@RequestBody @Validated UrlDto urlDto) {
        String url = urlDto.getUrl();
        log.info("Received request to make a redirect from a link: {}", url);

        validateUrlOnFormat(url);
        UrlDto dto = urlService.associateHashWithURL(urlDto);
        return generateShortUrl(dto);
    }

    private UrlDto generateShortUrl(UrlDto urlDto) {
        String shortUlr = serverAddress + urlDto.getHash();
        urlDto.setUrl(shortUlr);

        log.info("Short URL: {} are generated", shortUlr);
        return urlDto;
    }

    private void validateUrlOnFormat(String url) {
        try {
            new URL(url);
            log.info("URL passed format validation");
        } catch (MalformedURLException e) {
            throw new InvalidUrlException("Invalid URL format");
        }
    }
}
