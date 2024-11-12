package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public RedirectView redirectToLongUrl(@PathVariable String hash) {
        val url = urlService.getLongUrlByHash(hash);
        return new RedirectView(url);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createShortUrl(@RequestBody @Valid UrlDto urlDto) {
        return urlService.createShortUrl(urlDto);
    }
}