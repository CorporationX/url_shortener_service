package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class UrlController {
    private final UrlService urlService;

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public RedirectView redirectToOriginalUrl(@PathVariable String hash) {
        String url = urlService.getUrl(hash);
        return new RedirectView(url);
    }
}
