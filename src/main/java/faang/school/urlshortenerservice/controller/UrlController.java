package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.annotations.ValidUrl;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RequiredArgsConstructor
@Validated
@RequestMapping("url_shortener")
@RestController
public class UrlController {
    private final UrlService urlService;

    @GetMapping("/{hash}")
    public RedirectView getUrl(@PathVariable String hash) {
        String url = urlService.getUrl(hash);
        return new RedirectView(url);
    }

    @PostMapping()
    public String createHash(@RequestParam @ValidUrl String url) {
        return urlService.createHash(url);
    }
}
