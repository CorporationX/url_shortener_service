package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/shortcuts")
@AllArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public String convertToShortUrl(@RequestBody UrlDto urlDto) {
        return urlService.getHashFromUrl(urlDto);
    }
}