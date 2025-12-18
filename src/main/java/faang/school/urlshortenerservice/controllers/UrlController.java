package faang.school.urlshortenerservice.controllers;

import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.services.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/shortener", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/hash")
    public String getHashUrl(@RequestBody HashDto dto) {
        return urlService.createHashUrl(dto);
    }

    @GetMapping("/{hash}")
    public String getOriginalUrl(@PathVariable String hash) {
        return urlService.getOriginalUrl(hash);
    }
}
