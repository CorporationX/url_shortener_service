package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.request.FullUrlRequestDto;
import faang.school.urlshortenerservice.dto.request.ShortUrlRequestDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class UrlControllerImpl {

    private final UrlService urlService;

    @GetMapping
    public RedirectView redirectToOriginalUrl(@Valid @RequestBody FullUrlRequestDto dto) {
        String originalUrl = urlService.getFullUrl(dto.getHash());

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(originalUrl);

        return redirectView;
    }

    @PostMapping("${spring.controller.api}${spring.controller.version}/url")
    public String createShortUrl(@Valid @RequestBody ShortUrlRequestDto dto) {
        return urlService.createShortUrl(dto.getFullUrl());
    }
}
