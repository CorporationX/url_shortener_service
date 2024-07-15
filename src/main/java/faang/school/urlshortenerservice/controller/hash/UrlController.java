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
    public String transformUrlToHash(@RequestBody UrlDto urlDto) {
        urlValidator.validateUrl(urlDto.getUrl());
        return urlService.transformUrlToHash(urlDto.getUrl());
    }

    @GetMapping("/{hash}")
    public RedirectView redirect(@PathVariable HashDto hash) {
        String url = urlService.getUrlFromHash(hash.getHash());

        RedirectView redirectView = new RedirectView(url);
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }

}
