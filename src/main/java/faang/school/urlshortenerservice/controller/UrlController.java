package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlCreateDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.ValidationException;
import faang.school.urlshortenerservice.service.url.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.net.MalformedURLException;
import java.net.URL;

@Tag(name = "URL Controller")
@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new URL hash")
    public UrlDto createUrlHash(@RequestBody UrlCreateDto urlCreateDto) {

        URL url = parseUrl(urlCreateDto);
        return urlService.createUrlHash(url);
    }

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    @Operation(summary = "Redirect to URL from hash")
    public RedirectView getUrlFromHash(@PathVariable String hash) {

        String url = urlService.getUrlFromHash(hash);
        return new RedirectView(url);
    }

    private URL parseUrl(UrlCreateDto urlCreateDto) {

        URL url;
        try {
            url = new URL(urlCreateDto.getUrl());
        } catch (MalformedURLException e) {
            throw new ValidationException(e);
        }

        return url;
    }
}
