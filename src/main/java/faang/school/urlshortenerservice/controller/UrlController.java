package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validator.UrlValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {

    @Value("${url-path.path}")
    private String path;

    private final UrlValidator urlValidator;
    private final UrlService urlService;

    @PostMapping("/createShortUrl")
    public String createShortUrl(@RequestBody String url) {
        urlValidator.validateUrl(url);
        String hash = urlService.createShortUrl(url);
        return path + hash;
    }
}
