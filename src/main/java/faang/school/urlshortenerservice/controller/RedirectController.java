package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
public class RedirectController {
    private final UrlService urlService;

    @GetMapping("/{hash:[a-zA-Z0-9]{6}}")
    public RedirectView getOriginal(@PathVariable String hash) {
        String originalUrl = urlService.getOriginal(hash);

        return new RedirectView(originalUrl);
    }
}
