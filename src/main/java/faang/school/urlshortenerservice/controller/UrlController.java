package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("api/v1/urls")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/url")
    @ResponseStatus(HttpStatus.OK)
    public String getUrlHash(@Valid @RequestBody UrlDto urlDto) {
        return urlService.getUrlHash(urlDto);
    }

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public RedirectView getUrl(@PathVariable String hash) {
        String url = urlService.getOriginalUrl(hash);
        return new RedirectView(url);
    }

}
