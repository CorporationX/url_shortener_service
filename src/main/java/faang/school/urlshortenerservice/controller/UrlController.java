package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
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
    public String getUrl(@RequestBody @Valid UrlDto dto) {
        return urlService.getHash(dto);
    }

}
