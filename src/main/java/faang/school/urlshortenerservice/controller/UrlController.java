package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.DTO.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService changeUrlService;

    @PostMapping("/change-url")
    public UrlDto changeUrl(@RequestBody @Valid UrlDto request) {
        return changeUrlService.ShortUrl(request);
    }
}
