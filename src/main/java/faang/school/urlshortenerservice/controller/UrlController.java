package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.service.UrlShortenerService;
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
@RequestMapping("/api/v1/shortener")
@RequiredArgsConstructor
public class UrlController {

    private final UrlShortenerService urlShortenerService;
    private final UrlMapper urlMapper;

    @GetMapping("/{hash}")
    public RedirectView getUrl(@PathVariable String hash) {
        String longUrl = urlShortenerService.getUrl(hash);

        return new RedirectView(longUrl);
    }

    @PostMapping("/url")
    public String createShortLink(@Valid @RequestBody UrlDto urlDto) {
        Url url = urlMapper.toEntity(urlDto);
        return urlShortenerService.createShortLink(url);
    }
}
