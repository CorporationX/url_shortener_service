package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("${controller.general_url}")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public ShortUrlDto createShortUrl(@Valid @RequestBody UrlDto urlDto, HttpServletRequest httpServletRequest) {
        return urlService.createShortUrl(urlDto, httpServletRequest);
    }

    @GetMapping("/{hash}")
    public RedirectView redirectToUrl(@PathVariable String hash) {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(urlService.getUrl(hash).getUrl());
        redirectView.setStatusCode(HttpStatus.FOUND);

        return redirectView;
    }
}