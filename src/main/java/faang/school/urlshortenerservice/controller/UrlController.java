package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.UrlShortDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${base.url}/url")
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public UrlShortDto createShortUrl(@RequestBody @Valid UrlDto dto) {
        return urlService.createShortUrl(dto);
    }

    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("/hash/{hash}")
    public UrlDto createShortUrl(@PathVariable String hash) {
        return urlService.getLongUrl(hash);
    }
}
