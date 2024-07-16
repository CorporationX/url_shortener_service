package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validator.UrlValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/url")
public class UrlController {

    private final UrlService urlService;
    private final UrlValidator urlValidator;

    @PostMapping()
    public String createShortLink(@RequestBody @Valid UrlDto urlDto) {
        return urlService.createShortLink(urlDto);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<RedirectView> redirectToLongLink(@PathVariable("hash") String hash) {
        urlValidator.checkIsNullHash(hash);
        String longLink = urlService.getLongLink(hash);

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(longLink);

        return new ResponseEntity<>(redirectView, HttpStatus.FOUND);
    }
}
