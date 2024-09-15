package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;


@RestController
@RequiredArgsConstructor
@RequestMapping("/url")
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/short")
    public HashDto saveUrl(@RequestBody @Valid UrlDto urlDto) {
        return urlService.createShortLink(urlDto);
    }

    @GetMapping("/{hash}")
    public RedirectView redirectToUrl(@PathVariable String hash){
        String originalUrl = urlService.getOriginalUrlByHash(hash);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(originalUrl);
        return redirectView;
    }
}
