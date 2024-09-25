package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.WriteUrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/{hash}")
    public String redirect(@PathVariable String hash) {
        return "redirect:" + urlService.getUrlByHash(hash);
    }

    @PostMapping
    @ResponseBody
    public String createShortUrl(@Valid @RequestBody WriteUrlDto writeUrlDto) {
        String shortUrl = urlService.createShortUrl(writeUrlDto.getUrl());
        String baseUrl = ServletUriComponentsBuilder.fromCurrentRequest()
               // .replacePath(null)
                .build()
                .toUriString();
        return baseUrl + "/" + shortUrl;
    }
}