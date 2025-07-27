package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.config.hash.HashProperties;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@Validated
@RestController
@RequestMapping("${api_version}/url")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;
    private final HashProperties hashProperties;

    @PostMapping
    public UrlResponseDto createShortUrl(@Valid @RequestBody UrlRequestDto url) {
        return urlService.createShortUrl(url);
    }

    @GetMapping("/{hash}")
    public RedirectView getUrl(@PathVariable @Size(max = 6) String hash) {
        return createRedirectView(urlService.getUrl(hash).getUrl());
    }

    private RedirectView createRedirectView(String redirectUrl) {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectUrl);
        redirectView.setStatusCode(HttpStatus.FOUND);

        return redirectView;
    }
}

