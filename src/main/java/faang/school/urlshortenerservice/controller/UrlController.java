package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/{hash}")
    @ResponseStatus(value = HttpStatus.FOUND)
    public RedirectView getUrl(@PathVariable String hash) {
        return new RedirectView(urlService.getUrl(hash), true);
    }

    @PostMapping("/url")
    @ResponseStatus(value = HttpStatus.CREATED)
    public String postUrl(@RequestBody UrlDto urlDto) throws MalformedURLException, URISyntaxException {
        new URL(urlDto.getUrl()).toURI();
        log.info("URL is valid: {}",urlDto);
        return urlService.saveUrlGetHash(urlDto);
    }
}
