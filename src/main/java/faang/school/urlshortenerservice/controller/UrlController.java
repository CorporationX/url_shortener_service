package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Validated
public class UrlController {
    @Value("${baseUrl}")
    private String baseUrl;
    private final UrlService urlService;

    @PostMapping("/url")
    @ResponseStatus(HttpStatus.CREATED)
    public String createUrl(@Valid @RequestBody @NotNull UrlDto urlDto) {
        return baseUrl.concat(urlService.saveNewHash(urlDto));
    }

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public RedirectView getUrl(@PathVariable @NotNull String hash) {
        String largeUrl = urlService.findUrl(hash);
        return new RedirectView(Objects.requireNonNullElse(largeUrl, "/error-page"));
    }
}
