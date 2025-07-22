package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.OriginalUrl;
import faang.school.urlshortenerservice.dto.ShortUrl;
import faang.school.urlshortenerservice.service.UrlServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/shortener")
@RequiredArgsConstructor
public class ShortenerController {
    private final UrlServiceImpl urlService;

    @PostMapping
    public ShortUrl shorten(@RequestBody @Valid OriginalUrl originalUrl) {
        String hash = urlService.shorten(originalUrl);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/{hash}")
                .buildAndExpand(hash)
                .toUri();

        return new ShortUrl(location.toString());
    }
}
