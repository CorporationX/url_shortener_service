package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
public class UrlController {

    @Value("${app.domain}")
    private String domain;

    private final UrlCacheRepository urlCacheRepository;
    private final UrlService urlService;

    @PostMapping("/url")
    public String shorten(@Valid @RequestBody UrlDto urlDto) {
        return String.format("%s/%s", domain, urlService.getHash(urlDto.getUrl()));
    }

    @GetMapping("${app.domain}/{hash}")
    public RedirectView get(@PathVariable String hash) {
        return new RedirectView(urlService.getUrl(hash));
    }
}
