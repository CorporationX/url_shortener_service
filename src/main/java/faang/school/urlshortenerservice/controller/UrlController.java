package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
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
    public void createUrlHash(@RequestBody UrlDto urlDto) {

        URL url = parseUrl(urlDto);
        urlService.createUrlHash(url);
    }

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    @Operation(summary = "Redirect to URL from hash")
    public RedirectView getUrlFromHash(@PathVariable String hash) {

        String url = urlService.getUrlFromHash(hash);
        return new RedirectView(url);
    }

    private URL parseUrl(UrlDto urlDto) {

        URL url;
        try {
            url = new URL(urlDto.getUrl());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return url;
    }
}
