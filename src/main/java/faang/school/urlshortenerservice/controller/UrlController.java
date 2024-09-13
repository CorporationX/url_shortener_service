package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public UrlDto saveAndGetShortUrl(@RequestBody @Valid UrlDto urlDto){
        return urlService.saveAndGetShortUrl(urlDto);
    }

    @GetMapping("/hash/{hash}")
    public RedirectView get(@PathVariable("hash") String hash){
        var url = urlService.getUrl(hash);
        return new RedirectView(url, true);
    }
}
