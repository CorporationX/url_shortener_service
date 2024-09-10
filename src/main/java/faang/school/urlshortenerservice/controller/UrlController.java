package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @Value("${spring.url_repository.url_prefix}")
    private String urlPrefix;

    @PostMapping("/url")
    @ResponseStatus(HttpStatus.OK)
    public String save(@RequestBody @Valid UrlDto url) {
        return urlPrefix + urlService.save(url);
    }

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public RedirectView redirect(@PathVariable String hash) {
        String url = urlService.getUrl(hash);

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(url);
        return redirectView;
    }
}
