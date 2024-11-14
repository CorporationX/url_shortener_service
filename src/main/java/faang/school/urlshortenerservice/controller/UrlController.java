package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.CreateUrlDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public UrlResponseDto createHashUrl(@RequestBody @Valid CreateUrlDto dto) {
        String result = urlService.createHashUrl(dto.getUrl());

        return new UrlResponseDto(result);
    }
}
