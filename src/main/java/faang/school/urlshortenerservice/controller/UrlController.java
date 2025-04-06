package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ShortenRequest;
import faang.school.urlshortenerservice.dto.ShortenResponse;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/v1/url")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping()
    public ShortenResponse shorten(@RequestBody @Valid ShortenRequest shortenRequest) {
        return urlService.shorten(shortenRequest.getUrl());
    }

    @GetMapping("/{hash}")
    public RedirectView resolveAndRedirect(@PathVariable String hash) {
        String url = urlService.resolve(hash);

        return new RedirectView(url);
    }

}