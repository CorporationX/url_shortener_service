package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/url")
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/shorten")
    public UrlDto createShortLink(@Valid @RequestBody UrlDto urlDto) {
        return urlService.convertShortUrl(urlDto);
    }

    @GetMapping("/get/{hash}")
    public void getUrl(@PathVariable String hash, HttpServletResponse response) {
        Url url = urlService.getUrl(hash);
        response.setHeader("Location", url.getUrl());
        response.setStatus(302);
    }

}
