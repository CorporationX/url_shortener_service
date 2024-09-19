package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validator.UrlValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;
    private final UrlValidator urlValidator;

    @PostMapping("/url")
    @ResponseStatus(HttpStatus.OK)
    public String add(@RequestBody String url) {
        urlValidator.validate(url);
        return urlService.add(url);
    }
}