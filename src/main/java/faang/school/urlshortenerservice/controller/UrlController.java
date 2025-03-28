package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlCreateDto;
import faang.school.urlshortenerservice.dto.UrlReadDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@Validated
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public UrlReadDto createdShortUrl(@RequestBody @Validated UrlCreateDto urlCreateDto){
        return urlService.createShortUrl(urlCreateDto);
    }

    @GetMapping("/{hash}")
    public ModelAndView redirectLongUrl(
            @PathVariable
            @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Неверный тип хэша")
            String hash) {
        String originalUrl = urlService.getOriginalUrl(hash);
        return new ModelAndView("redirect:" + originalUrl);
    }
}
