package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validator.UrlValidator;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService service;
    private final UrlValidator validator;

    @PostMapping("/url")
    public String shortenUrl(@RequestBody @NotBlank String url) {
        validator.validateUrl(url);

        return service.shortenUrl(url);
    }
}