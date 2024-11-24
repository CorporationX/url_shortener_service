package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.UrlNotValidException;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/url")
public class UrlController {
    private final UrlService urlService;
    private final UrlValidator urlValidator = new UrlValidator();

    @PostMapping("/shortener")
    public UrlDto createShortUrl(@RequestBody UrlDto urlDto) {
       if (!urlValidator.isValid(urlDto.getUrl())) {
           throw new UrlNotValidException("Url not valid: " + urlDto.getUrl());
       }
       return urlService.createUrlHash(urlDto);
    }

    @GetMapping("/{hash}")
    public RedirectView getShortUrl(@PathVariable String hash) {
        return new RedirectView(urlService.getUrl(hash));
    }
}
