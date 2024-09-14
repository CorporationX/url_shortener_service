package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/{hash}")
    @ResponseStatus(value = HttpStatus.FOUND)
    public ModelAndView getUrl(@PathVariable String hash) {
        return new ModelAndView("redirect:" + urlService.getUrl(hash));
    }

    @PostMapping("/url")
    @ResponseStatus(value = HttpStatus.CREATED)
    public String postUrl(@RequestBody UrlDto urlDto) throws MalformedURLException, URISyntaxException {
        if (!isValidURL(urlDto.getUrl())) {
            throw new RuntimeException();
        }
        return urlService.createHashCache(urlDto);
    }

    boolean isValidURL(String url) throws MalformedURLException, URISyntaxException {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }
}
