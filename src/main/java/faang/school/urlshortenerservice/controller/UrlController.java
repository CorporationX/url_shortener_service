package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlReq;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.utils.UrlUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Validated
@RestController
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.URLS)
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public String createShortUrl(@RequestBody @Valid UrlReq urlReq) {
        return urlService.createShortUrl(urlReq);
    }

    @GetMapping(UrlUtils.HASH)
    public RedirectView getOriginalUrl(@PathVariable("hash") @NotBlank String hash) {
        return new RedirectView(urlService.getOriginalUrl(hash));
    }
}
