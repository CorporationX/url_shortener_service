package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/url/shortener/")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("")
    public String makeShortUrl(@RequestBody UrlDto urlDto) {
        return urlService.makeShortUrl(urlDto);
    }

    @GetMapping("")
    public RedirectView getOriginalUrl(@RequestParam String shortUrl) {
        String longUrl = urlService.getOriginalUrl(shortUrl);
        return new RedirectView(longUrl);
    }
}
