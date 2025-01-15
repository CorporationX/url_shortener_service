package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validator.UrlValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/url")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;
    private final UrlValidator urlValidator;

    @PostMapping("/shorten")
    public UrlDto shorten(@RequestBody @Valid UrlDto dto){
        urlValidator.validate(dto.getUrl());
        return urlService.shortenUrl(dto);
    }
}
