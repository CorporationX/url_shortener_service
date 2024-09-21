package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/redirect/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public void getUrl(@PathVariable String hash, HttpServletResponse response) {
        String url = urlService.getUrl(hash);
        response.setHeader("Location", url);
    }

    @PostMapping("/url")
    public String createUrl(@Validated @RequestBody UrlDto url) {
        return urlService.createUrl(url);
    }
}
