package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@RestController
@RequiredArgsConstructor
@Slf4j

public class UrlController {

    private final UrlService urlService;

    @PostMapping("/url")
    @ResponseStatus(HttpStatus.CREATED)
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

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public void getOriginalUrl(@PathVariable String hash, HttpServletResponse response) throws IOException {
        String originalUrl = urlService.getOriginalUrl(hash);
        response.sendRedirect(originalUrl);
    }
}
