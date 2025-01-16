package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
public class UrlController {
    private final UrlService urlService;

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @PostMapping("/url")
    public UrlDto shorten(@RequestBody @Valid UrlDto dto){
        log.info("Request to shorten url: {}", dto.getUrl());
        return urlService.shortenUrl(dto);
    }

    @GetMapping("/{hash}")
    public RedirectView redirect(@PathVariable @NotBlank String hash) {
        log.info("Request to redirect url: {}", hash);
        return new RedirectView(urlService.getUrl(hash));
    }
}