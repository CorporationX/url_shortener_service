package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/shortener/urls")
public class UrlController {
    private final UrlService urlService;

    @PostMapping()
    public ShortUrlDto createShortUrl(@RequestBody @Valid UrlDto urlDto) {
        return urlService.createShortUrl(urlDto);
    }

    @GetMapping("/{hash}")
    public RedirectView getUrl(@PathVariable @NotNull String hash) {
        UrlDto urlDto = urlService.getUrl(hash);
        if (urlDto == null || urlDto.url() == null || urlDto.url().isBlank()) {
            return new RedirectView("/error/404");
        }
        return new RedirectView(urlDto.url());
    }
}
