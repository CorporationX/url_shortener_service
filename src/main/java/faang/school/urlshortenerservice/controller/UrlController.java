package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/url")
    public UrlDto createShortLink(@RequestBody UrlDto urlDto) {
        isValidUrl(urlDto.getUrl());
        return urlService.shortenUrl(urlDto);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<String> getUrl(@PathVariable String hash) throws UrlNotFoundException {
        Optional<Url> url = urlService.getUrl(hash);
        return new ResponseEntity<>(url.get().getUrl(), HttpStatusCode.valueOf(302));
    }

    private void isValidUrl(String url) {
        if (!url.contains("https://")) {
            throw new DataValidationException("The passed argument is not a url");
        }
    }

}
