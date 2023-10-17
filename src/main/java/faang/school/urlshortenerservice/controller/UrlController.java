package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.InvalidUrlException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

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

        String shortUlr = serverAddress + dto.getHash();
        dto.setUrl(shortUlr);

        log.info("Short URL: {} are generated", shortUlr);
        return dto;
    }

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public RedirectView redirectToOriginalUrl(@PathVariable String hash) {
        Url originalUrl = urlService.getOriginalUrl(hash);
        return new RedirectView(Objects.requireNonNullElse(originalUrl, "/").toString());
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
