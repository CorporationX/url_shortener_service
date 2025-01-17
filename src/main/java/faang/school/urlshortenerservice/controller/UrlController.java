package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/url-shortener")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.PERMANENT_REDIRECT)
    public void getUrl(@PathVariable String hash, HttpServletResponse response) {
        try {
            String url = urlService.getOriginalUrl(hash);
            response.sendRedirect(url);
        } catch (IOException e) {
            log.error("Redirect error", e);
            throw new RuntimeException("Internal server error");
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UrlDto createShortUrl(@Valid @RequestBody UrlDto url) {
        return urlService.createShortUrl(url);
    }
}