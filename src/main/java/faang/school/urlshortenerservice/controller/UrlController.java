package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @Value("${spring.url_repository.url_prefix}")
    private String urlPrefix;

    @PostMapping("/url")
    @ResponseStatus(HttpStatus.OK)
    public String save(@RequestBody @Valid UrlDto url) {
        return urlPrefix + urlService.save(url);
    }
}
