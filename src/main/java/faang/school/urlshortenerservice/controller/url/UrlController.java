package faang.school.urlshortenerservice.controller.url;

import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.service.url.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/url")
public class UrlController {

    @Value("${url.shortener.prefix:http://corporation/}")
    private String prefix;
    private final UrlService urlService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String shortenUrl(@RequestBody @Valid UrlDto urlDto) {
        return prefix + urlService.shortenUrl(urlDto);
    }
}
