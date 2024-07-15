package faang.school.urlshortenerservice.controller.hash;

import faang.school.urlshortenerservice.dto.hash.HashDto;
import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.service.url.UrlService;
import faang.school.urlshortenerservice.validator.url.UrlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/urlManager")
public class UrlController {
    private final UrlService urlService;
    private final UrlValidator urlValidator;

    @PostMapping("/url")
    @ResponseStatus(HttpStatus.CREATED)
    public String transformUrlToHash(@RequestBody UrlDto urlDto) {
        log.info("Received URL for transformation: {}", urlDto.getUrl());
        urlValidator.validateUrl(urlDto.getUrl());
        log.info("URL validated successfully: {}", urlDto.getUrl());
        String hash = urlService.transformUrlToHash(urlDto.getUrl());
        log.info("Transformed URL to hash: {}", hash);
        return hash;
    }

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.OK)
    public RedirectView redirect(@PathVariable HashDto hash) {
        log.info("Received request to redirect for hash: {}", hash.getHash());
        String url = urlService.getUrlFromHash(hash.getHash());

        RedirectView redirectView = new RedirectView(url);
        redirectView.setStatusCode(HttpStatus.FOUND);
        log.info("Redirecting to URL: {}", url);
        return redirectView;
    }
}
