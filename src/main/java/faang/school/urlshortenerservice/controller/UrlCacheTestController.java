package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/api/url")
@RequiredArgsConstructor
public class UrlCacheTestController {
    private final UrlCacheRepository cacheRepository;
    @GetMapping("/{hash}")
    public UrlDto getUrl(@PathVariable("hash") String hash) {
        return new UrlDto(cacheRepository.getUrl(hash).orElseThrow().getUrl());
    }
}
