package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlShortenerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${controller.mapping.url_shortener}")
@Validated
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    @PostMapping("/url")
    @CacheEvict(value = "url", key = "#url")
    public UrlDto shortenUrl(@RequestBody @Valid UrlDto url) {
        return urlShortenerService.shortenUrl(url);
    }
    //should I return here Status 200 or 3xx (redirection)


    @GetMapping("/{hash}")
    @Cacheable(value = "url", key = "#hash")
    @ResponseStatus(HttpStatus.MOVED_TEMPORARILY)
    public UrlDto getOriginalLink(@Size(max = 6) @PathVariable String hash) {
        return urlShortenerService.getOriginalLink(hash);
    }
    //Or is it better to use here RedirectView ?
}