package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.RequestDto;
import faang.school.urlshortenerservice.dto.HashResponseDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hash")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public HashResponseDto getHash(@RequestBody @Valid RequestDto dto) {
        return urlService.getHash(dto);
    }

    @GetMapping("/{hash}")
    public UrlResponseDto getUrl(@PathVariable("hash") String hash) {
        return urlService.getUrl(hash);
    }

}
