package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;
    @PostMapping("/url")
    public UrlDto createShortUrl (@RequestBody UrlDto urlDto) {
        return urlService.generateShortUrl(urlDto);
    }
}
