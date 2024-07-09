package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validator.UrlValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Validated
@RestController
@RequiredArgsConstructor
public class UrlController {
    private static final int RESPONSE_STATUS_CODE = 302;
    private final UrlService service;
    private final UrlValidator validator;

    @PostMapping("/url")
    public String shortenUrl(@RequestBody @NotBlank String url) {
        validator.validateUrl(url);

        return service.shortenUrl(url);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<String> getUrl(@PathVariable @Size(min = 6, max = 6) String hash) {
        String url = service.getUrl(hash);
        return new ResponseEntity<>(url, HttpStatusCode.valueOf(RESPONSE_STATUS_CODE));
    }
}