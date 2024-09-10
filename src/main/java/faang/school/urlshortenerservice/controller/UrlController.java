package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/url")
@RequiredArgsConstructor
@Validated
public class UrlController {
private final UrlService urlService;

@GetMapping("/{hash}")
    public String createUrl(@PathVariable String hash) {
    return urlService.findUrl(hash);
}

@PostMapping("/url")
    public UrlDto createShortUrl(@RequestBody UrlDto url) {
    return urlService.convertUrl(url);
}

}
