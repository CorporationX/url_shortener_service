package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RedirectView> getUrl(@PathVariable String hash) {
        String longUrl = urlService.getLongUrl(hash);
        RedirectView redirectView = new RedirectView(longUrl);
        redirectView.setStatusCode(HttpStatus.FOUND);
        return new ResponseEntity<>(redirectView, HttpStatus.FOUND);
    }
}
