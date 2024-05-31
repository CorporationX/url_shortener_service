package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/url")
    public RedirectView shortenUrl(@Valid @RequestBody UrlDto urlDto) {
        String shortUrl = urlService.createShortenUrl(urlDto.getLongUrl());
        RedirectView redirectView = new RedirectView(shortUrl);
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }

    @GetMapping("{hash}")
    public RedirectView createLongUrl(@PathVariable String hash){
        String longUrl = urlService.getLongUrl(hash);
        RedirectView redirectView = new RedirectView(longUrl);
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }
}
