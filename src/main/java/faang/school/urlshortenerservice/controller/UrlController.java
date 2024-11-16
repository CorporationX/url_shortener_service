package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.UrlNotExistException;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class UrlController {
    private final UrlService urlService;
    private final UrlValidator urlValidator = new UrlValidator();

    @PostMapping("/url")
    public UrlDto createShortUrl(@Validated @RequestBody UrlDto urlDto) {
       if (!urlValidator.isValid(urlDto.getUrl())) {
           throw new UrlNotExistException("Url not exist: " + urlDto.getUrl());
       }
       return urlService.createUrlHash(urlDto);
    }

    @GetMapping("/{hash}")
    public RedirectView getShortUrl(@PathVariable String hash) {
        return new RedirectView(urlService.getUrl(hash));
    }
}
