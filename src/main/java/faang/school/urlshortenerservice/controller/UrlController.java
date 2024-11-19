package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/")
public class UrlController {
    private final UrlService urlService;

    @GetMapping("/{hash}")
    @ResponseStatus(code = HttpStatus.PERMANENT_REDIRECT)
    public RedirectView redirect(@PathVariable String hash) {
        Url url = urlService.getOriginalUrl(hash);
        return new RedirectView(url.getUrl());
    }

    @PostMapping("/shortUrl")
    public String getShortUrl(@RequestBody @Valid UrlDto urlDto) {
        return urlService.makeShortUrl(urlDto.getUrl());
    }
}
