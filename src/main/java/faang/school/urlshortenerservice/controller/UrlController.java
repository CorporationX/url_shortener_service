package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlCreateDto;
import faang.school.urlshortenerservice.dto.UrlReadDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequiredArgsConstructor
@Validated
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/url")
    public UrlReadDto createShortUrl(@RequestBody @Validated UrlCreateDto urlDto) {
        return urlService.createShortUrl(urlDto);
    }

    @GetMapping("/{hash}")
    public ModelAndView redirectToLongUrl(
            @PathVariable
            @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Неверный тип хэша")
            String hash) {
        String originalUrl = urlService.getOriginalUrl(hash);
        return new ModelAndView("redirect:" + originalUrl);
    }
}
