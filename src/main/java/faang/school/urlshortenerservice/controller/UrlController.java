package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    @ResponseStatus(HttpStatus.CREATED)
    public UrlDto shorten(@RequestBody UrlDto url) {
        String longUrl = url.getUrl();
        String shortUrl = urlService.shorten(longUrl);
        return new UrlDto(shortUrl);
    }

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public RedirectView getUrls(@PathVariable String hash) {
        String longUrl = urlService.getUrl(hash);
        return new RedirectView(longUrl);
    }
}
