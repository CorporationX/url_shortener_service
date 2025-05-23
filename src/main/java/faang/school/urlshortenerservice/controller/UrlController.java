package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ShortUrlRequestDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/")
@SuppressWarnings("unused")
public class UrlController {

    private final UrlService urlService;

    @PostMapping("url")
    public String getShortUrl(@Valid @RequestBody ShortUrlRequestDto requestDto) {
        return urlService.getShortUrl(requestDto);
    }

    @GetMapping("{hash}")
    public void redirectToOriginalUrl(@PathVariable("hash") String shortLink, HttpServletResponse response) {
        var url = urlService.redirectToOriginalUrl(shortLink);

        response.setStatus(302);
        response.setHeader("Location", url);
    }
}
