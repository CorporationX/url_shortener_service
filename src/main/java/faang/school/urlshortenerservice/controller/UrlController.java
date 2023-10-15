package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/url")
public class UrlController {
    private final UrlService urlService;

    @PostMapping({"/url"})
    public void getShortUrl(@RequestBody UrlDto urlDto) {
        urlService.getShortUrl(urlDto);
    }

    @GetMapping("/{hash}")
    public RedirectView redirect(@PathVariable @NotBlank String hash) {
        return new RedirectView(urlService.getUrl(hash));
    }
}
