package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.config.properties.url.UrlProperties;
import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.service.url.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/urls")
public class UrlController {

    private final UrlService urlService;
    private final UrlProperties urlProperties;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String getShortUrl(@Valid @RequestBody LongUrlDto url) {
        return urlProperties.getUrlShort().getBaseUrl() + urlService.saveAndConvertLongUrl(url);
    }
}
