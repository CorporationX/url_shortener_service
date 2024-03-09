package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/urls")
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public String createShortUrl(@Valid @RequestBody UrlDto url) {
        return urlService.createShortUrl(url);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<RedirectView> redirectLongUrl(@PathVariable String hash) {
        String url = urlService.redirectLongUrl(hash);
        RedirectView redirectView = new RedirectView(url);
        return new ResponseEntity<>(redirectView, HttpStatusCode.valueOf(302));
    }
}
