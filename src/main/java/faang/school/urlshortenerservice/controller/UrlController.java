package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService service;

    @PostMapping("/url")
    public String shortenUrl(@RequestBody @NotBlank String url) {
        if (!isValid(url)) {
            throw new DataValidationException("Passed url isn't valid. Please check your url.");
        }

        return service.shortenUrl(url);
    }

    private boolean isValid(String value) {
        if (value == null) {
            return false;
        }

        try {
            new URI(value);
        } catch (URISyntaxException e) {
            return false;
        }

        return true;
    }
}