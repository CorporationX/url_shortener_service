package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlCreatedRequest;
import faang.school.urlshortenerservice.dto.UrlCreatedResponse;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("url")
    public UrlCreatedResponse getUrl(@RequestBody UrlCreatedRequest url) {
        return urlService.createUrl(url);
    }

    @GetMapping("{hash}")
    public RedirectView redirectToUrl(@PathVariable String hash) {
        return new RedirectView(urlService.getUrl(hash));
    }
}
