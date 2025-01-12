package faang.school.urlshortenerservice.controller.url_shortener;

import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.service.url_shortener.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class UrlShortenerController {

    private final UrlService urlService;

    @PostMapping()
    public UrlDto shortenUrl(@RequestBody @Valid UrlDto urlDto) {
        return urlService.shortenUrl(urlDto);
    }
}
