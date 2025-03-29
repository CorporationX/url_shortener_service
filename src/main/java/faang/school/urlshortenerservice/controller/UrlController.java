package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.Dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/urls")
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public String createShortLink(@RequestBody @URL @NotBlank UrlDto urlDto) {
        return urlService.createShortLink(urlDto);
    }

    @GetMapping("/{hash}")
    public RedirectView redirectToOriginalUrl(@PathVariable @NotNull String hash) {
        String url = urlService.getOriginalUrl(hash);
        RedirectView redirectView = new RedirectView(url);
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }
}
