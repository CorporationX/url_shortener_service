package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping
@RequiredArgsConstructor
public class UrlController {
    @Value("${prefix}")
    private String protocol;
    private final UrlService urlService;

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public RedirectView getHash(@PathVariable String hash) {
        String longUrl = urlService.searchUrl(hash);
        return new RedirectView(longUrl);
    }

    @PostMapping("/url")
    @ResponseStatus(HttpStatus.CREATED)
    public String getShortUrl(@RequestBody UrlDto urlDto) {
        return protocol + urlService.saveNewHash(urlDto);
    }
}
