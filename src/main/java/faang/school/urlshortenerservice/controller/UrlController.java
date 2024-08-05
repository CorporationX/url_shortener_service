package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validator.UrlValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;
    private final UrlValidator urlValidator;

    @GetMapping("{hash}")
    public RedirectView getOriginUrl(@PathVariable String hash) {
        Url realUrl = urlService.getOriginUrl(hash);
        RedirectView redirectView = new RedirectView(realUrl.getUrl());
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }

    @PostMapping("url")
    @ResponseStatus(HttpStatus.CREATED)
    public UrlDto getShortUrl(@RequestBody UrlDto urlDto) {
        urlValidator.isValid(urlDto.getUrl());
        return urlService.getShortUrl(urlDto);
    }
}
