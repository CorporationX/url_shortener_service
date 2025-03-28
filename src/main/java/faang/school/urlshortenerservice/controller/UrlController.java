package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/v1/url")
@RequiredArgsConstructor
@Slf4j
public class UrlController {
    private final UrlService urlService;

    @GetMapping("/{hash}")
    public RedirectView redirectToOriginalUrl(@PathVariable String hash) {
        String originalUrl = urlService.getOriginalUrl(hash);
        log.info("Перенаправление хэша {} на URL {}", hash, originalUrl);

        RedirectView redirectView = new RedirectView(originalUrl);
        redirectView.setStatusCode(HttpStatus.FOUND);

        return redirectView;
    }
}
