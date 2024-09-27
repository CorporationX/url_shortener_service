package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController()
@RequiredArgsConstructor
@Validated
@Slf4j
public class UrlController {
    private final UrlService urlService;

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public RedirectView redirectToOriginalUrl(@PathVariable String hash) {
        String url = urlService.findUrl(hash).getUrl();
        return redirectToUrl(url);
    }

    @PostMapping("/url")
    public UrlDto createShortUrl(@RequestBody UrlDto url) {

        return urlService.convertToShortUrl(url);
    }

    private RedirectView redirectToUrl(String url) {
        log.info("redirecting to url: {}", url);
        return new RedirectView(url);
    }

}
