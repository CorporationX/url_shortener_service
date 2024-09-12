package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/urls")
public class UrlController {
    private final UrlService urlService;
    @PostMapping
    public UrlDto createShortUrl(@RequestBody @Valid UrlDto urlDto) {
        return urlService.createShortUrl(urlDto);
    }

    @GetMapping("/hashes/{hash}")
    public RedirectView getUrl(@PathVariable String hash) {
        return urlService.getUrl(hash);
    }
}
