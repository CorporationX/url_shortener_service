package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/urls")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/short")
    @ResponseStatus(HttpStatus.CREATED)
    public HashDto saveUrl(@RequestBody @Valid UrlDto urlDto) {
        return urlService.createShortLink(urlDto);
    }
}
