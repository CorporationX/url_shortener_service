package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletResponseWrapper;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@RestController
@RequestMapping("/url")
@Slf4j
@Data
@RequiredArgsConstructor
@Validated
public class UrlController {
    private final UrlService urlService;

    @PostMapping()
    public String createShortUrl(@RequestBody @Valid UrlDto urlDto) {
        log.info("Received a new request to create a short URL: " + urlDto.getUrl());
        return urlService.createShortUrl(urlDto);
    }

    @GetMapping("/{hash}")
    public RedirectView getUrl(@PathVariable String hash) {
        log.info("Received a request to retrieve an original URL: " + hash);
        String redirectUrl = urlService.getUrl(hash);
        return new RedirectView(redirectUrl);
    }
}
