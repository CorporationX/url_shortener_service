package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.UrlDto;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validator.UrlValidator;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;
    private final UrlValidator urlValidator;

    @GetMapping("{hash}")
    @ResponseStatus(HttpStatus.OK)
    public void getOriginUrl(@PathVariable String hash, HttpServletResponse response) {
        Url realUrl = urlService.getOriginUrl(hash);
        response.setHeader("Location", realUrl.getUrl());
        response.setStatus(302);
    }

    @PostMapping("url")
    @ResponseStatus(HttpStatus.CREATED)
    public UrlDto getShortUrl(@RequestBody UrlDto urlDto) {
        urlValidator.isValid(urlDto.getUrl());
        return urlService.getShortUrl(urlDto);
    }
}
