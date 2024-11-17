package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.config.properties.url.UrlProperties;
import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.service.url.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.regex.Pattern;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/urls")
public class UrlController {

    private final UrlService urlService;
    private final UrlProperties urlProperties;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String getShortUrl(@Valid @RequestBody LongUrlDto url) {
        return urlProperties.getUrlShort().getBaseUrl() + urlService.saveAndConvertLongUrl(url);
    }

    @GetMapping("/{shortUrl}")
    public void redirect(@PathVariable String shortUrl, HttpServletResponse response) {
        if (!Pattern.matches(urlProperties.getUrlShort().getUrlRegex(), shortUrl)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid URL format");
        }
        Optional<String> url = urlService.retrieveLongUrl(shortUrl);
        if (url.isPresent()) {
            response.setStatus(HttpServletResponse.SC_FOUND);
            response.setHeader("Location", String.valueOf(url));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "URL not found");
        }
    }
}
