package faang.school.urlshortenerservice.controller.url;

import faang.school.urlshortenerservice.model.dto.request.url.ShortenRequest;
import faang.school.urlshortenerservice.model.dto.response.url.ShortenResponse;
import faang.school.urlshortenerservice.service.url.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/v1/url")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping()
    public ShortenResponse shorten(@RequestBody @Valid ShortenRequest shortenRequest) {
        return urlService.shorten(shortenRequest.getUrl());
    }

    @GetMapping("/{hash}")
    public RedirectView resolveAndRedirect(@PathVariable String hash) {
        String url = urlService.resolve(hash);

        return new RedirectView(url);
    }

}
