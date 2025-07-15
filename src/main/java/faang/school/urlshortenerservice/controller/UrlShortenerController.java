package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlShortenerDto;
import faang.school.urlshortenerservice.service.UrlShortenerService;
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
@RequestMapping("api/v1/urlShortener")
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    @PostMapping
    public String shortenUrl(@RequestBody @Valid UrlShortenerDto urlShortenerDto) {
        return urlShortenerService.create(urlShortenerDto);
    }

    @GetMapping("/{hash}")
    public String findUrlByHash(@PathVariable String hash) {
        return "redirect:" + urlShortenerService.findUrlByHash(hash);
    }
}
