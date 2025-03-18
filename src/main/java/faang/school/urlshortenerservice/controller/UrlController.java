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

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/shortener/urls")
public class UrlController {
    UrlService urlService;

    @PostMapping()
    public ShortUrlDto createShortUrl(@RequestBody @Valid UrlDto urlDto) {
        return urlService.createShortUrl(urlDto);
    }

    @GetMapping("/{hash}")
    public UrlDto getUrl(@PathVariable @NotNull String hash) {
        return urlService.getUrl(hash);
    }

    /*
    String originalUrl = urlService.getOriginalUrl(shortUrl);
        try {
            response.sendRedirect(originalUrl);
        } catch (IOException e) {
            log.error("Error sending redirect to {}", originalUrl, e);
            throw new RedirectException("Error sending redirect to " + originalUrl);
        }
     */
}
