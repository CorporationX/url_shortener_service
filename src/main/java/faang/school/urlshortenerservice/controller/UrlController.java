package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.urlValidation.URLValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    private final URLValidator urlValidator;

    @PostMapping
    public UrlDto createShortUrl(@RequestBody UrlDto urlDto) {
        urlValidator.isValidURL(urlDto.getLink());
        return urlService.createShortUrl(urlDto);
    }

    @GetMapping("/{hash}")
    public RedirectView getRedirectView(@PathVariable("hash") String hash) {
        RedirectView redirectView = urlService.getRedirectView(hash);
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }
}
