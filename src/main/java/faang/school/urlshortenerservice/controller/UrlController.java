package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.url.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;


@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @GetMapping("/{hash}")
    public RedirectView redirectToOriginal(@PathVariable String hash) {
        return new RedirectView(urlService.getOriginalUrl(hash));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createShort(@RequestBody @Valid UrlDto urlDto) {
        return urlService.createShort(urlDto);
    }
}
