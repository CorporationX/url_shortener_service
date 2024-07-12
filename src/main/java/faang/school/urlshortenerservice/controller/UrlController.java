package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;
    private final UserContext userContext;

    @Value("${url.static-address}")
    private String httpStaticAddress;

    @PostMapping
    public String createShortLink(@RequestBody @Valid UrlDto urlDto) {
        return httpStaticAddress + urlService.createShortLink(urlDto.getUrl());
    }

    @GetMapping("/{hash}")
    public ModelAndView getOriginalUrlByHash(@PathVariable String hash) {
        return new ModelAndView("redirect:" + urlService.getOriginalUrlByHash(hash));
    }
}
