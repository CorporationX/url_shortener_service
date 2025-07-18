package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
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

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public UrlDto createShortUrl(@Validated @RequestBody UrlDto url) {
        return urlService.createShortUrl(url);
    }

    @GetMapping("/{hash}")
    public RedirectView getUrl(@PathVariable String hash) {
        return createRedirectView(urlService.getUrl(hash).getUrl());
    }

    private RedirectView createRedirectView(String redirectUrl) {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectUrl);
        redirectView.setStatusCode(HttpStatus.FOUND);

        return redirectView;
    }
}

