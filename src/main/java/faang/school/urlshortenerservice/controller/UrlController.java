package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validate.UrlValidate;
import jakarta.validation.Valid;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService service;
    private final UrlValidate urlValidate;

    @PostMapping
    public void generateShortUrl(@RequestBody @Valid UrlDto originalUrl) {
        urlValidate.getUrlValidate(originalUrl);
        service.generateShortUrl(originalUrl);
    }
    @GetMapping
    public RedirectView returnFullUrl(@NotNull @RequestParam String requesthash){
        String redirectUrl = service.returnFullUrl(requesthash);
        RedirectView redirectView = new RedirectView(redirectUrl);
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }
}