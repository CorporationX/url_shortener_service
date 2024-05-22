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

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/shortener")
public class UrlController {
    private final UrlShortenerService urlShortenerService;

    @PostMapping("/url")
    @CacheEvict(value = "url", key = "#url")
    public UrlDto convertToShortUrl(@RequestBody @Valid UrlDto urlDto) {
        return urlShortenerService.convertToShortUrl(urlDto);
    }

    @GetMapping("/{hash}")
    @Cacheable(value = "url", key = "#hash")
    @ResponseStatus(HttpStatus.MOVED_TEMPORARILY)
    public UrlDto getOriginalUrl(@Size(max = 6) @PathVariable String hash) {
        return urlShortenerService.getOriginalUrl(hash);
    }

}
