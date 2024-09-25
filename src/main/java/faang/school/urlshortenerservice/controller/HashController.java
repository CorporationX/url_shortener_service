package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.HashService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HashController {
    private final HashService hashService;

    @PostMapping("/url")
    @ResponseStatus(HttpStatus.OK)
    public String getShortUrl(@Valid @RequestBody UrlDto urlDto) {
        return hashService.getShortUrl(urlDto.getUrl());
    }

    @GetMapping("/hash/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public RedirectView getOriginalUrl(@PathVariable @NotEmpty String hash) {
        RedirectView redirectView = new RedirectView();
        String originalUrl = hashService.getOriginalUrl(hash);
        redirectView.setUrl(originalUrl);
        return redirectView;
    }
}
