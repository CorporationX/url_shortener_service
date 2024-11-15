package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.UrlNotExistException;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
