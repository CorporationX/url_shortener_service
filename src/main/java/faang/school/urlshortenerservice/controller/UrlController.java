package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/url")
public class UrlController {
    private final UrlService urlService;
    @PostMapping
    public String getShortUrl(@RequestBody UrlDto urlDto){
        return urlService.createShortUrl(urlDto);
    }

    @GetMapping("/{shortLink}")
    public RedirectView getRedirectView(@PathVariable("shortLink") String shortLink) {
        return new RedirectView(urlService.getRedirectView(shortLink));
    }
}
